package net.gekidolukas.showyouridentity.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.gekidolukas.showyouridentity.client.NameTagRenderState;
import net.gekidolukas.showyouridentity.data.IdentityData;
import net.gekidolukas.showyouridentity.data.IdentityEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import com.mojang.blaze3d.vertex.PoseStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends net.minecraft.client.renderer.entity.LivingEntityRenderer<AbstractClientPlayer, net.minecraft.client.model.PlayerModel<AbstractClientPlayer>>{
    public PlayerRendererMixin(EntityRendererProvider.Context context, PlayerModel<AbstractClientPlayer> entityModel, float f) {
        super(context, entityModel, f);
    }


    @Inject(
            method = "renderNameTag(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;renderNameTag(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", ordinal = 1)
    )
    private void liftRealNameTag(AbstractClientPlayer player, Component component, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
        poseStack.pushPose();
        IdentityData identityData = IdentityData.get(player.level());
        IdentityEntry entry = identityData.getIdentity(player);
        if(entry != null && entry.getPronouns() != null && !entry.getPronouns().isEmpty()) {
            poseStack.translate(0.0F, 0.125F, 0.0F);
        }
    }

    @Inject(
            method = "renderNameTag(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;renderNameTag(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", ordinal = 1, shift = At.Shift.AFTER)
    )
    private void renderPronounsPhysicallyUnder(AbstractClientPlayer player, Component component, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
        poseStack.popPose();

        IdentityData identityData = IdentityData.get(player.level());
        IdentityEntry entry = identityData.getIdentity(player);

        if (entry != null && entry.getPronouns() != null && !entry.getPronouns().isEmpty()) {
            Component pronouns = Component.literal("")
                    .append(Component.literal("-=").withStyle(ChatFormatting.GRAY))
                    .append(Component.literal(entry.getPronouns()).withStyle(ChatFormatting.GOLD))
                    .append(Component.literal("=-").withStyle(ChatFormatting.GRAY))
                    ;
            NameTagRenderState.renderingPronouns = true;
            super.renderNameTag(player, pronouns, poseStack, multiBufferSource, i);
            NameTagRenderState.renderingPronouns = false;

        }
    }

    @WrapOperation(
            method = "renderNameTag(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V")
    )
    private void editScoreboardHeight(PoseStack instance, float x, float y, float z, Operation<Void> original, @Local(argsOnly = true) AbstractClientPlayer player) {
        IdentityData identityData = IdentityData.get(player.level());
        IdentityEntry entry = identityData.getIdentity(player);

        if(entry != null) {
            original.call(instance,x,0.2F,z);
        } else {
            original.call(instance,x,y,z);
        }
    }
}
