package net.gekidolukas.showyouridentity.fabric.mixin;


import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.gekidolukas.showyouridentity.data.IdentityData;
import net.gekidolukas.showyouridentity.data.IdentityEntry;
import net.gekidolukas.showyouridentity.data.PrideFlag;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "eu.pb4.styledchat.StyledChatStyles")
public class StyledChatStylesMixin {


    @ModifyReturnValue(
            method = "getDisplayName",
            at = @At(value = "RETURN"))
    private static Component appendPronouns(Component original, ServerPlayer player, Component vanillaDisplayName) {

        IdentityData identityData = IdentityData.get(player.level());
        IdentityEntry entry = identityData.getIdentity(player);

        if(entry != null) {
            ResourceLocation defaultFont = ResourceLocation.parse("minecraft:default");

            Component pronouns = Component.literal(" ")
                    .append(Component.literal("- ").withStyle(ChatFormatting.GRAY))
                    .append(Component.literal(entry.getPronouns()).withStyle(ChatFormatting.GOLD))
                    ;

            return PrideFlag.applyChatFlags(original, entry.getPrimaryFlag() ,entry.getSecondaryFlag()).copy().append(pronouns.copy().withStyle(style -> style.withFont(defaultFont)));
        }


        return original;
    }
}
