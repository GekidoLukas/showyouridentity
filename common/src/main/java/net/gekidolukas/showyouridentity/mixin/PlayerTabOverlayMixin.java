package net.gekidolukas.showyouridentity.mixin;

import com.mojang.authlib.GameProfile;
import net.gekidolukas.showyouridentity.client.ClientToggles;
import net.gekidolukas.showyouridentity.data.IdentityData;
import net.gekidolukas.showyouridentity.data.IdentityEntry;
import net.gekidolukas.showyouridentity.data.PrideFlag;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerTabOverlay.class)
public class PlayerTabOverlayMixin {

    @Inject(
            method = "getNameForDisplay",
            at = @At("RETURN"),
            cancellable = true
    )
    private void appendPronounsToTab(PlayerInfo playerInfo, CallbackInfoReturnable<Component> cir) {
        if(!ClientToggles.shouldRenderTab) return;
        Component originalName = cir.getReturnValue();
        if (originalName == null) return;

        GameProfile profile = playerInfo.getProfile();
        Minecraft mc = Minecraft.getInstance();

        if (profile != null && mc.level != null) {
            Player player = mc.level.getPlayerByUUID(profile.getId());

            if (player != null) {
                IdentityData identityData = IdentityData.get(player.level());
                IdentityEntry entry = identityData.getIdentity(player);
                ResourceLocation defaultFont = ResourceLocation.parse("minecraft:default");

                if (entry != null && entry.getPronouns() != null && !entry.getPronouns().isEmpty()) {
                    Component pronouns = Component.literal(" ")
                            .append(Component.literal("- ").withStyle(ChatFormatting.GRAY))
                            .append(Component.literal(entry.getPronouns()).withStyle(ChatFormatting.GOLD))
                            ;

                    cir.setReturnValue(PrideFlag.applyTabFlags(originalName, entry.getPrimaryFlag() ,entry.getSecondaryFlag()).copy().append(pronouns.copy().withStyle(style -> style.withFont(defaultFont))));
                }
            }
        }
    }
}
