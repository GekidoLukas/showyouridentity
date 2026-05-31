package net.gekidolukas.showyouridentity.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.gekidolukas.showyouridentity.client.AvatarRenderStateExtension;
import net.gekidolukas.showyouridentity.client.NameTagRenderState;
import net.gekidolukas.showyouridentity.data.IdentityData;
import net.gekidolukas.showyouridentity.data.IdentityEntry;
import net.gekidolukas.showyouridentity.data.NameFlagPos;
import net.gekidolukas.showyouridentity.data.PrideFlag;
import net.minecraft.ChatFormatting;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.network.chat.Component;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
public abstract class AvatarRendererMixin<AvatarlikeEntity extends Avatar & ClientAvatarEntity> extends LivingEntityRenderer<AvatarlikeEntity, AvatarRenderState, PlayerModel>  {
    public AvatarRendererMixin(EntityRendererProvider.Context context, PlayerModel entityModel, float f) {
        super(context, entityModel, f);
    }

    @Inject(
            method = "submitNameTag(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitNameTag(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/phys/Vec3;ILnet/minecraft/network/chat/Component;ZIDLnet/minecraft/client/renderer/state/CameraRenderState;)V", ordinal = 1)
    )
    private void liftRealNameTag(AvatarRenderState avatarRenderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState, CallbackInfo ci) {
        poseStack.pushPose();
        IdentityEntry entry = ((AvatarRenderStateExtension) avatarRenderState).showyouridentity$getIdentity();
        if (entry != null && entry.getPronouns() != null && !entry.getPronouns().isEmpty()) {
            poseStack.translate(0.0F, 0.125F, 0.0F);
        }
    }

    @Inject(
            method = "submitNameTag(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitNameTag(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/phys/Vec3;ILnet/minecraft/network/chat/Component;ZIDLnet/minecraft/client/renderer/state/CameraRenderState;)V", ordinal = 1, shift = At.Shift.AFTER)
    )
    private void renderPronounsPhysicallyUnder(AvatarRenderState avatarRenderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState, CallbackInfo ci) {
        poseStack.popPose();

        IdentityEntry entry = ((AvatarRenderStateExtension) avatarRenderState).showyouridentity$getIdentity();

        if (entry != null && entry.getPronouns() != null && !entry.getPronouns().isEmpty()) {
            Component pronouns = Component.literal("")
                    .append(Component.literal("-=").withStyle(ChatFormatting.GRAY))
                    .append(Component.literal(entry.getPronouns()).withStyle(ChatFormatting.GOLD))
                    .append(Component.literal("=-").withStyle(ChatFormatting.GRAY));
            NameTagRenderState.renderingPronouns = true;
            submitNodeCollector.submitNameTag(poseStack, avatarRenderState.nameTagAttachment, 0, pronouns, !avatarRenderState.isDiscrete, avatarRenderState.lightCoords, avatarRenderState.distanceToCameraSq, cameraRenderState);
            super.submitNameTag(avatarRenderState, poseStack, submitNodeCollector, cameraRenderState);
            NameTagRenderState.renderingPronouns = false;
        }
    }

    @WrapOperation(
            method = "submitNameTag(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V")
    )
    private void editScoreboardHeight(PoseStack instance, float x, float y, float z, Operation<Void> original, @Local(argsOnly = true) AvatarRenderState avatarRenderState) {
        IdentityEntry entry = ((AvatarRenderStateExtension) avatarRenderState).showyouridentity$getIdentity();

        if(entry != null) {
            original.call(instance,x,0.2F,z);
        } else {
            original.call(instance,x,y,z);
        }

    }

    @Inject(
            method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V",
            at = @At("TAIL")
    )
    private void extractRenderState(AvatarlikeEntity avatar, AvatarRenderState avatarRenderState, float f, CallbackInfo ci) {
        if (avatarRenderState.nameTag != null && avatar instanceof Player player) {
            IdentityEntry entry = IdentityData.get(player.level()).getIdentity(player);

            ((AvatarRenderStateExtension) avatarRenderState).showyouridentity$setIdentity(entry);

            if (entry != null) {
                boolean isRenderingFlag = (NameTagRenderState.renderingPronouns && entry.getFlagPos() == NameFlagPos.PRONOUNS) ||
                        (!NameTagRenderState.renderingPronouns && entry.getFlagPos() == NameFlagPos.PLAYER_NAME);

                if (isRenderingFlag) {
                    PrideFlag leftFlag = entry.getPrimaryFlag();
                    PrideFlag rightFlag = entry.getSecondaryFlag();

                    avatarRenderState.nameTag = PrideFlag.applyOverHeadFlags(avatarRenderState.nameTag, leftFlag ,rightFlag);
                }
            }
        }
    }
}
