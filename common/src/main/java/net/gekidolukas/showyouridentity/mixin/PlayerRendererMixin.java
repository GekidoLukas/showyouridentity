package net.gekidolukas.showyouridentity.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.gekidolukas.showyouridentity.SYIConfig;
import net.gekidolukas.showyouridentity.data.IdentityData;
import net.gekidolukas.showyouridentity.data.IdentityEntry;
import net.gekidolukas.showyouridentity.data.NameFlagPos;
import net.gekidolukas.showyouridentity.data.PrideFlag;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends net.minecraft.client.renderer.entity.LivingEntityRenderer<AbstractClientPlayer, net.minecraft.client.model.PlayerModel<AbstractClientPlayer>>{

    public PlayerRendererMixin(EntityRendererProvider.Context context, PlayerModel<AbstractClientPlayer> entityModel, float f) {
        super(context, entityModel, f);
    }

    /**
     * This method is a slightly modified copy of {@link net.minecraft.client.renderer.entity.EntityRenderer#renderNameTag}
     * It renders the pronouns underneath the Player's name the same way a scoreboard objective is rendered underneath, however it can scale the text
     */
    @Unique
    private void showyouridentity$renderPronouns(Entity entity, Component component, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, float f) {
        if(!SYIConfig.renderPronounsWithNameTag) return;
        double d = this.entityRenderDispatcher.distanceToSqr(entity);
        if (!(d > (double)4096.0F)) {
            Vec3 vec3 = entity.getAttachments().getNullable(EntityAttachment.NAME_TAG, 0, entity.getViewYRot(f));
            if (vec3 != null) {
                boolean bl = !entity.isDiscrete();
                int j = "deadmau5".equals(component.getString()) ? -10 : 0;
                poseStack.pushPose();
                poseStack.translate(vec3.x, vec3.y , vec3.z);
                poseStack.translate(0,(SYIConfig.pronounScale * 0.25f) + 0.27f,0); //Somehow this formula works, idk
                poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
                poseStack.scale(0.025F, -0.025F, 0.025F);
                poseStack.scale(SYIConfig.pronounScale, SYIConfig.pronounScale, SYIConfig.pronounScale);
                Matrix4f matrix4f = poseStack.last().pose();
                float g = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
                int k = (int)(g * 255.0F) << 24;
                Font font = this.getFont();
                float h = (float)(-font.width(component) / 2);
                font.drawInBatch(component, h, (float)j, 553648127, false, matrix4f, multiBufferSource, bl ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL, k, i);
                if (bl) {
                    font.drawInBatch(component, h, (float)j, -1, false, matrix4f, multiBufferSource, Font.DisplayMode.NORMAL, 0, i);
                }

                poseStack.popPose();
            }
        }
    }

    @Inject(
            method = "renderNameTag(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;renderNameTag(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V", ordinal = 1)
    )
    private void liftRealNameTag(AbstractClientPlayer player, Component component, PoseStack poseStack, MultiBufferSource buffer, int i, float f, CallbackInfo ci) {
        poseStack.pushPose();
        IdentityData identityData = IdentityData.get(player.level());
        IdentityEntry entry = identityData.getIdentity(player);
        if(entry != null && entry.getPronouns() != null && !entry.getPronouns().isEmpty() && SYIConfig.renderPronounsWithNameTag) {
            poseStack.translate(0.0F, (SYIConfig.pronounScale * 0.275f) + 0.01f, 0.0F); //Somehow this formula works, idk
        }
    }

    @WrapOperation(
            method = "renderNameTag(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;renderNameTag(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V", ordinal = 1)
    )
    private void applyPronouns(PlayerRenderer instance, Entity entity, Component component, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, float v, Operation<Void> original) {
        if(entity instanceof Player player){
            IdentityData identityData = IdentityData.get(player.level());
            IdentityEntry entry = identityData.getIdentity(player);
            if(entry != null && SYIConfig.renderFlagsWithNameTag) {
                if(entry.getFlagPos() == NameFlagPos.PLAYER_NAME) {
                    PrideFlag leftFlag = entry.getPrimaryFlag();
                    PrideFlag rightFlag = entry.getSecondaryFlag();

                    original.call(instance,entity,PrideFlag.applyFlagsToComponent(component, leftFlag ,rightFlag,false),poseStack,multiBufferSource,i,v);
                    return;
                }
            }
        }
        original.call(instance,entity,component,poseStack,multiBufferSource,i,v);
    }

    @Inject(
            method = "renderNameTag(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;renderNameTag(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V", ordinal = 1, shift = At.Shift.AFTER)
    )
    private void renderPronounsUnderneathName(AbstractClientPlayer player, Component component, PoseStack poseStack, MultiBufferSource buffer, int i, float f, CallbackInfo ci) {
        poseStack.popPose();

        IdentityData identityData = IdentityData.get(player.level());
        IdentityEntry entry = identityData.getIdentity(player);

        if (entry != null && entry.getPronouns() != null && !entry.getPronouns().isEmpty()) {
            MutableComponent pronouns = Component.literal("")
                    .append(Component.literal("-=").withStyle(ChatFormatting.GRAY))
                    .append(Component.literal(entry.getPronouns()).withStyle(ChatFormatting.GOLD))
                    .append(Component.literal("=-").withStyle(ChatFormatting.GRAY))
                    ;

            if(!SYIConfig.renderPronounsWithNameTag) pronouns = Component.empty();

            if(entry.getFlagPos() == NameFlagPos.PRONOUNS && SYIConfig.renderFlagsWithNameTag) {
                PrideFlag leftFlag = entry.getPrimaryFlag();
                PrideFlag rightFlag = entry.getSecondaryFlag();
                showyouridentity$renderPronouns(player,PrideFlag.applyFlagsToComponent(pronouns, leftFlag ,rightFlag,false),poseStack,buffer,i,f);
            } else {
                showyouridentity$renderPronouns(player,pronouns,poseStack,buffer,i,f);
            }

        }
    }

}
