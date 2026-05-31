package net.gekidolukas.showyouridentity.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.gekidolukas.showyouridentity.data.NameFlagPos;
import net.gekidolukas.showyouridentity.data.PrideFlag;
import net.gekidolukas.showyouridentity.client.NameTagRenderState;
import net.gekidolukas.showyouridentity.data.IdentityData;
import net.gekidolukas.showyouridentity.data.IdentityEntry;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {



    @Inject(
            method = "renderNameTag",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V", shift = At.Shift.AFTER)
    )
    private void scalePronounsTag(T entity, Component component, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
        if (NameTagRenderState.renderingPronouns) {
            poseStack.scale(0.5F, 0.5F, 1.0F);
            poseStack.translate(0,10,0);
        }
    }


    @ModifyVariable(
            method = "renderNameTag",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private Component modifyPlayerNameTag(Component displayName, T entity) {
        if (entity instanceof Player player) {

            IdentityData identityData = IdentityData.get(player.level());
            IdentityEntry entry = identityData.getIdentity(player);

            if (entry != null ) {
                boolean isRenderingFlag = (NameTagRenderState.renderingPronouns && entry.getFlagPos() == NameFlagPos.PRONOUNS) ||
                        (!NameTagRenderState.renderingPronouns && entry.getFlagPos() == NameFlagPos.PLAYER_NAME);

                if(isRenderingFlag) {
                    PrideFlag leftFlag = entry.getPrimaryFlag();
                    PrideFlag rightFlag = entry.getSecondaryFlag();

                    return PrideFlag.applyOverHeadFlags(displayName, leftFlag ,rightFlag);
                }

            }
        }

        return displayName;
    }




}
