package net.gekidolukas.showyouridentity.util;

import net.gekidolukas.showyouridentity.data.IdentityData;
import net.gekidolukas.showyouridentity.data.IdentityEntry;
import net.gekidolukas.showyouridentity.data.PrideFlag;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.UUID;


public class PlayerNameModifier {

    /**
     * This method can modify a Player's name Component to have pronouns and flags
     * @param original the original player name
     * @param uuid the player's uuid
     * @param level the level the player is in that context. It is used to get the {@link net.gekidolukas.showyouridentity.data.IdentityData}
     * @param applyPronouns used for the pronoun rendering toggles provided by the {@link net.gekidolukas.showyouridentity.SYIConfig}
     * @param applyFlags used for the flag rendering toggles provided by the {@link net.gekidolukas.showyouridentity.SYIConfig}
     * @param applyInGeneral used for the server overrides provided by the {@link net.gekidolukas.showyouridentity.client.ClientToggles}
     * @return the modified name or the original, if the toggles turn it off completely
     */
    public static Component modifyName(Component original, UUID uuid, Level level, boolean applyPronouns, boolean applyFlags, boolean applyInGeneral){
        if(!applyPronouns && !applyFlags) return original;
        if(!applyInGeneral) return original;

        if (uuid != null && level != null) {
            IdentityData identityData = IdentityData.get(level);
            IdentityEntry entry = identityData.getIdentity(uuid);
            if (entry != null) {
                ResourceLocation defaultFont = ResourceLocation.parse("minecraft:default");
                MutableComponent pronouns = Component.empty();
                if(applyPronouns && entry.getPronouns() != null && !entry.getPronouns().isEmpty()) {
                    pronouns = Component.literal(" ")
                            .append(Component.literal("- ").withStyle(ChatFormatting.GRAY))
                            .append(Component.literal(entry.getPronouns()).withStyle(ChatFormatting.GOLD));
                }

                return (applyFlags ? PrideFlag.applyFlagsToComponent(original, entry.getPrimaryFlag() ,entry.getSecondaryFlag(),true) : original).copy().append(pronouns.copy().withStyle(style -> style.withFont(defaultFont)));
            }
        }
        return original;
    }
}
