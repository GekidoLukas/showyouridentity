package net.gekidolukas.showyouridentity.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.gekidolukas.showyouridentity.client.NameTagRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.NameTagFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NameTagFeatureRenderer.Storage.class)
public abstract class NameTagFeatureRendererStorage {
    @Inject(
            method = "add",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V", shift = At.Shift.AFTER)
    )
    private void scalePronounsTag(PoseStack poseStack, Vec3 vec3, int i, Component component, boolean bl, int j, double d, CameraRenderState cameraRenderState, CallbackInfo ci) {
        if (NameTagRenderState.renderingPronouns) {
            poseStack.scale(0.5F, 0.5F, 1.0F);
            poseStack.translate(0,10,0);
        }
    }
}
