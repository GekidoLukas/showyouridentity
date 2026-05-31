package net.gekidolukas.showyouridentity.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.gekidolukas.showyouridentity.client.AvatarRenderStateExtension;
import net.gekidolukas.showyouridentity.data.NameFlagPos;
import net.gekidolukas.showyouridentity.data.PrideFlag;
import net.gekidolukas.showyouridentity.client.NameTagRenderState;
import net.gekidolukas.showyouridentity.data.IdentityEntry;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity, S extends EntityRenderState> {
    @Inject(
            method = "submitNameTag",
            at = @At("HEAD") // TODO ?
    )
    private void scalePronounsTag(S entityRenderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState, CallbackInfo ci) {
        if (NameTagRenderState.renderingPronouns) {
            poseStack.scale(0.5F, 0.5F, 1.0F);
            poseStack.translate(0,10,0);
        }
    }
}
