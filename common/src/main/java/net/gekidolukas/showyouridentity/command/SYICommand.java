package net.gekidolukas.showyouridentity.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.gekidolukas.showyouridentity.data.*;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;

import java.util.Arrays;
import java.util.List;

public class SYICommand {

    private static final List<String> subjectivePronouns = Arrays.asList("He","She","They","It");
    private static final List<String> objectivePronouns = Arrays.asList("Him","Her","Them","It");


    private static final SuggestionProvider<CommandSourceStack> PRONOUNS =((context, builder) ->  {
        builder.suggest("Any Pronouns");
        builder.suggest("Ask");
        for(int i = 0; i < subjectivePronouns.size(); i++) {
            builder.suggest(subjectivePronouns.get(i) + "/" + objectivePronouns.get(i));
        }
        for(var first : subjectivePronouns) {
            for(var second : subjectivePronouns) {
                if(!first.equals(second)) {
                    builder.suggest(first+ "/" +second);
                }
            }
        }

        return builder.buildFuture();
    });

    private static final SuggestionProvider<CommandSourceStack> FLAGS =((context, builder) ->  {
        for(var value : PrideFlag.values()) {
            if(!value.getId().isEmpty()) {
                builder.suggest(value.getId().toLowerCase());
            }
        }
        return builder.buildFuture();
    });

    private static final SuggestionProvider<CommandSourceStack> FLAG_POSITIONS =((context, builder) ->  {
        for(var value : NameFlagPos.values()) {
            if(!value.getId().isEmpty()) {
                builder.suggest(value.getId().toLowerCase());
            }
        }
        return builder.buildFuture();
    });

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("syi")
                .then(Commands.literal("pronouns")
                                .then(Commands.literal("set")
                                        .then(Commands.argument("value", StringArgumentType.greedyString()).suggests(PRONOUNS)
                                                .executes(commandContext -> executeSetPronouns(commandContext.getSource(),StringArgumentType.getString(commandContext,"value")))
                                        )
                                )
                                .then(Commands.literal("get")
                                        .then(Commands.argument("player", EntityArgument.player())
                                                .executes(commandContext -> executeGetPronouns(commandContext.getSource(),EntityArgument.getPlayer(commandContext,"player")))
                                        )
                                )
                        )
                .then(Commands.literal("flag")
                        .then(Commands.literal("set")
                                .then(Commands.literal("primary")
                                        .then(Commands.argument("value", StringArgumentType.word()).suggests(FLAGS)
                                                .executes(commandContext -> executeSetFlag(commandContext.getSource(),StringArgumentType.getString(commandContext,"value"),true))
                                        )
                                )
                                .then(Commands.literal("secondary")
                                        .then(Commands.argument("value", StringArgumentType.word()).suggests(FLAGS)
                                                .executes(commandContext -> executeSetFlag(commandContext.getSource(),StringArgumentType.getString(commandContext,"value"),false))
                                        )
                                )
                        )
                        .then(Commands.literal("position")
                                .then(Commands.argument("value", StringArgumentType.word()).suggests(FLAG_POSITIONS)
                                        .executes(commandContext -> executeSetFlagPos(commandContext.getSource(),StringArgumentType.getString(commandContext,"value")))
                                )
                        )
                        .then(Commands.literal("reset")
                                .then(Commands.literal("primary")
                                        .executes(commandContext -> executeResetFlagPlayer(commandContext.getSource(),true))

                                )
                                .then(Commands.literal("secondary")
                                        .executes(commandContext -> executeResetFlagPlayer(commandContext.getSource(),false))

                                )
                        )
                        .then(Commands.literal("info")
                                .then(Commands.argument("value", StringArgumentType.word()).suggests(FLAGS)
                                        .executes(commandContext -> executeFlagInfo(commandContext.getSource(),StringArgumentType.getString(commandContext,"value")))
                                )
                        )
                )
        );

        dispatcher.register(Commands.literal("syi")
                .then(Commands.literal("reset").requires(stack -> stack.permissions().hasPermission(new Permission.HasCommandLevel(PermissionLevel.GAMEMASTERS)))
                        .then(Commands.literal("all")
                                .executes(commandContext -> executeResetAll(commandContext.getSource()))
                        )
                        .then(Commands.literal("player")
                                .then(Commands.argument("name",EntityArgument.player())
                                        .executes(commandContext -> executeResetPlayer(commandContext.getSource(),EntityArgument.getPlayer(commandContext,"name")))
                                )
                        )
                )
                .then(Commands.literal("slur_filter").requires(stack -> stack.permissions().hasPermission(new Permission.HasCommandLevel(PermissionLevel.GAMEMASTERS)))
                        .then(Commands.literal("add")
                                .then(Commands.argument("slur",StringArgumentType.word())
                                        .executes(commandContext ->executeSlurFilterAddSlur(commandContext.getSource(),StringArgumentType.getString(commandContext,"slur")))
                                )
                        )
                        .then(Commands.literal("reload")
                                .executes(commandContext -> executeSlurFilterReload(commandContext.getSource()))
                        )
                )
        );
    }

    private static int executeSetPronouns(CommandSourceStack source, String input) {


        if(SlurFilter.isSevereSlur(input)) {
            source.sendFailure(Component.translatable("commands.showyouridentity.pronouns.set.failure.contains_slur",input));
            return 2;
        }

        //Length filter
        if(input.length() > 30) {
            input = input.substring(0,29);
        }

        final String pronouns = input;


        if(source.isPlayer()) {
            ServerPlayer player = source.getPlayer();
            IdentityData identityData = IdentityData.get(source.getLevel());
            IdentityEntry entry = identityData.getIdentity(player);

            if(entry == null) {
                entry = new IdentityEntry(pronouns, PrideFlag.NONE,PrideFlag.NONE, NameFlagPos.PLAYER_NAME);
                identityData.putIdentity(player, entry);
                identityData.sync(source.getLevel());
                source.sendSuccess(() -> Component.translatable("commands.showyouridentity.pronouns.set.success",pronouns),false);

            } else {
                if(!entry.getPronouns().equals(pronouns)) {
                    entry.setPronouns(pronouns);
                    identityData.markNeoDirty();
                    identityData.sync(source.getLevel());
                        source.sendSuccess(() -> Component.translatable("commands.showyouridentity.pronouns.set.success",pronouns),false);
                } else {
                    source.sendFailure(Component.translatable("commands.showyouridentity.pronouns.set.failure.already_these_pronouns",pronouns));
                }
            }


        } else {
            source.sendFailure(Component.translatable("commands.showyouridentity.pronouns.set.failure.not_a_player"));
        }

        return 1;
    }

    private static int executeGetPronouns(CommandSourceStack source, ServerPlayer player) {

        IdentityData identityData = IdentityData.get(source.getLevel());
        IdentityEntry entry = identityData.getIdentity(source.getPlayer());

        if(entry != null) {
            source.sendSuccess(() -> Component.translatable("commands.showyouridentity.pronouns.get.success", player.getScoreboardName(),Component.literal(entry.getPronouns()).withStyle(ChatFormatting.GOLD)),false);
        } else {
            source.sendFailure(Component.translatable("commands.showyouridentity.pronouns.get.failure", player.getScoreboardName()));
        }



        return 1;
    }

    private static int executeSetFlag(CommandSourceStack source, String inputFlag, boolean isPrimary) {


        if(!PrideFlag.isKnownFlag(inputFlag)) {
            source.sendFailure(Component.translatable("commands.showyouridentity.flag.set.failure.unknown"));
            return 2;
        }


        PrideFlag flag = PrideFlag.byId(inputFlag);



        if(source.isPlayer()) {
            ServerPlayer player = source.getPlayer();
            IdentityData identityData = IdentityData.get(source.getLevel());
            IdentityEntry entry = identityData.getIdentity(player);

            if(entry == null) {
                entry = new IdentityEntry("", isPrimary ? flag : PrideFlag.NONE, isPrimary ? PrideFlag.NONE : flag, NameFlagPos.PLAYER_NAME);
                identityData.putIdentity(player, entry);
                identityData.sync(source.getLevel());
                if(isPrimary) {
                    source.sendSuccess(() -> Component.translatable("commands.showyouridentity.flag.set_primary.success",flag.getId().toLowerCase()),false);
                } else {
                    source.sendSuccess(() -> Component.translatable("commands.showyouridentity.flag.set_secondary.success",flag.getId().toLowerCase()),false);
                }


            } else {
                PrideFlag currentFlag = isPrimary ? entry.getPrimaryFlag() : entry.getSecondaryFlag();
                if(!currentFlag.equals(flag)) {
                    if(isPrimary) {
                        entry.setPrimaryFlag(flag);
                        identityData.markNeoDirty();
                        identityData.sync(source.getLevel());
                        source.sendSuccess(() -> Component.translatable("commands.showyouridentity.flag.set_primary.success",flag.getId().toLowerCase()),false);
                    } else {
                        entry.setSecondaryFlag(flag);
                        identityData.markNeoDirty();
                        identityData.sync(source.getLevel());
                        source.sendSuccess(() -> Component.translatable("commands.showyouridentity.flag.set_secondary.success",flag.getId().toLowerCase()),false);
                    }
                } else {
                    if(isPrimary) {
                        source.sendFailure(Component.translatable("commands.showyouridentity.flag.set.failure.already_that_primary_flag",flag.getId().toLowerCase()));
                    } else  {
                        source.sendFailure(Component.translatable("commands.showyouridentity.flag.set.failure.already_that_secondary_flag",flag.getId().toLowerCase()));
                    }
                }
            }


        } else {
            source.sendFailure(Component.translatable("commands.showyouridentity.pronouns.set.failure.not_a_player"));
        }

        return 1;
    }

    private static int executeSetFlagPos(CommandSourceStack source, String inputPos) {


        if(!NameFlagPos.isKnownPos(inputPos)) {
            source.sendFailure(Component.translatable("commands.showyouridentity.flag_pos.set.failure.unknown"));
            return 2;
        }


        NameFlagPos flagPos = NameFlagPos.byId(inputPos);


        if(source.isPlayer()) {
            ServerPlayer player = source.getPlayer();
            IdentityData identityData = IdentityData.get(source.getLevel());
            IdentityEntry entry = identityData.getIdentity(player);

            if(entry == null) {
                entry = new IdentityEntry("", PrideFlag.NONE, PrideFlag.NONE, flagPos);
                identityData.putIdentity(player, entry);
                identityData.sync(source.getLevel());
                source.sendSuccess(() -> Component.translatable("commands.showyouridentity.flag_pos.set.success",flagPos.getId().toLowerCase()),false);

            } else {
                if(!entry.getFlagPos().equals(flagPos)) {
                    entry.setFlagPos(flagPos);
                    identityData.markNeoDirty();
                    identityData.sync(source.getLevel());
                    source.sendSuccess(() -> Component.translatable("commands.showyouridentity.flag_pos.set.success",flagPos.getId().toLowerCase()),false);
                } else {
                    source.sendFailure(Component.translatable("commands.showyouridentity.flag_pos.set.failure.already_that_flag_pos",flagPos.getId().toLowerCase()));
                }
            }


        } else {
            source.sendFailure(Component.translatable("commands.showyouridentity.pronouns.set.failure.not_a_player"));
        }

        return 1;
    }

    private static int executeResetFlagPlayer(CommandSourceStack source, boolean isPrimary) {


        PrideFlag flag = PrideFlag.NONE;



        if(source.isPlayer()) {
            ServerPlayer player = source.getPlayer();
            IdentityData identityData = IdentityData.get(source.getLevel());
            IdentityEntry entry = identityData.getIdentity(player);

            if(entry == null) {
                entry = new IdentityEntry("", isPrimary ? flag : PrideFlag.NONE, isPrimary ? PrideFlag.NONE : flag, NameFlagPos.PLAYER_NAME);
                identityData.putIdentity(player, entry);
                identityData.sync(source.getLevel());
                if(isPrimary) {
                    source.sendSuccess(() -> Component.translatable("commands.showyouridentity.flag.reset_primary.success",flag.getId().toLowerCase()),false);
                } else {
                    source.sendSuccess(() -> Component.translatable("commands.showyouridentity.flag.reset_secondary.success",flag.getId().toLowerCase()),false);
                }


            } else {
                PrideFlag currentFlag = isPrimary ? entry.getPrimaryFlag() : entry.getSecondaryFlag();
                if(!currentFlag.equals(flag)) {
                    if(isPrimary) {
                        entry.setPrimaryFlag(flag);
                        identityData.markNeoDirty();
                        identityData.sync(source.getLevel());
                        source.sendSuccess(() -> Component.translatable("commands.showyouridentity.flag.reset_primary.success",flag.getId().toLowerCase()),false);
                    } else {
                        entry.setSecondaryFlag(flag);
                        identityData.markNeoDirty();
                        identityData.sync(source.getLevel());
                        source.sendSuccess(() -> Component.translatable("commands.showyouridentity.flag.reset_secondary.success",flag.getId().toLowerCase()),false);
                    }
                } else {
                    if(isPrimary) {
                        source.sendFailure(Component.translatable("commands.showyouridentity.flag.reset.failure.already_reset"));
                    } else  {
                        source.sendFailure(Component.translatable("commands.showyouridentity.flag.reset.failure.already_reset"));
                    }
                }
            }


        } else {
            source.sendFailure(Component.translatable("commands.showyouridentity.pronouns.set.failure.not_a_player"));
        }

        return 1;
    }


    private static int executeFlagInfo(CommandSourceStack source, String inputFlag) {


        if(!PrideFlag.isKnownFlag(inputFlag)) {
            source.sendFailure(Component.translatable("commands.showyouridentity.flag.set.failure.unknown"));
            return 2;
        }


        PrideFlag flag = PrideFlag.byId(inputFlag);

        MutableComponent response = Component.empty();
        response.append(Component.literal(flag.getUnicode()).withStyle(style -> style.withFont(PrideFlag.PRIDE_FONT)));
        response.append(Component.literal(" ").withStyle(style -> style.withFont(FontDescription.DEFAULT)));
        response.append(Component.translatable(flag.getFlagNameTranslationKey()).withStyle(style -> style.withFont(FontDescription.DEFAULT).withColor(ChatFormatting.GOLD)));
        response.append(Component.literal(" ").withStyle(style -> style.withFont(FontDescription.DEFAULT)));
        response.append(Component.literal(flag.getUnicode()).withStyle(style -> style.withFont(PrideFlag.PRIDE_FONT)));
        response.append(Component.literal("\n").withStyle(style -> style.withFont(FontDescription.DEFAULT)));
        response.append(Component.translatable(flag.getFlagDescriptionTranslationKey()).withStyle(style -> style.withFont(FontDescription.DEFAULT).withColor(ChatFormatting.WHITE)));
        response.append(Component.literal("\n\n").withStyle(style -> style.withFont(FontDescription.DEFAULT)));
        response.append(Component.translatable(flag.getFlagCreditTranslationKey()).withStyle(style -> style.withFont(FontDescription.DEFAULT).withColor(ChatFormatting.GRAY)));


        source.sendSuccess(() -> response,false);

        return 1;
    }

    private static int executeResetAll(CommandSourceStack source) {

        IdentityData identityData = IdentityData.get(source.getLevel());
        identityData.reset();
        identityData.sync(source.getLevel());
        source.sendSuccess(() -> Component.translatable("commands.showyouridentity.reset.all.success"),false);

        return 1;
    }

    private static int executeResetPlayer(CommandSourceStack source,ServerPlayer player) {

        IdentityData identityData = IdentityData.get(source.getLevel());
        IdentityEntry entry = identityData.getIdentity(player);

        if(entry != null) {
            identityData.removeIdentity(player);
            identityData.sync(source.getLevel());
            source.sendSuccess(() -> Component.translatable("commands.showyouridentity.reset.player.success",player.getDisplayName()),false);
        } else {
            source.sendFailure(Component.translatable("commands.showyouridentity.reset.player.failure",player.getDisplayName()));
        }



        return 1;
    }

    private static int executeSlurFilterReload(CommandSourceStack source) {

        SlurFilter.initialize();
        source.sendSuccess(() -> Component.translatable("commands.showyouridentity.slur_filter.reload.success",SlurFilter.filterSize()),false);

        return 1;
    }
    private static int executeSlurFilterAddSlur(CommandSourceStack source,String slur) {


        if(SlurFilter.addSlur(slur)) {
            source.sendSuccess(() -> Component.translatable("commands.showyouridentity.slur_filter.add.success"),false);
        } else  {
            source.sendFailure(Component.translatable("commands.showyouridentity.slur_filter.add.failure"));
        }



        return 1;
    }
}
