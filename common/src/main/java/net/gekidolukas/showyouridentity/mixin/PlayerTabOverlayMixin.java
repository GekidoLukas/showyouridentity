package net.gekidolukas.showyouridentity.mixin;

import net.gekidolukas.showyouridentity.SYIConfig;
import net.gekidolukas.showyouridentity.client.ClientToggles;
import net.gekidolukas.showyouridentity.util.PlayerNameModifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
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
        cir.setReturnValue(PlayerNameModifier.modifyName(cir.getReturnValue(), playerInfo.getProfile().getId(),Minecraft.getInstance().level,SYIConfig.renderPronounsInTabList,SYIConfig.renderFlagsInTabList,ClientToggles.shouldRenderTab));
    }
}
