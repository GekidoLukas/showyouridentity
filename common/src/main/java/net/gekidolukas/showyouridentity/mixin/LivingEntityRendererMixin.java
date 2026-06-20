package net.gekidolukas.showyouridentity.mixin;

import net.gekidolukas.showyouridentity.SYIConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {

    @Inject(method = "shouldShowName(Lnet/minecraft/world/entity/LivingEntity;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;renderNames()Z"), cancellable = true)
    private void thirdPersonNameTagRendererInject(LivingEntity livingEntity, CallbackInfoReturnable<Boolean> cir) {
        if(SYIConfig.renderThirdPersonNameTag) {
            Minecraft mc = Minecraft.getInstance();
            cir.setReturnValue(Minecraft.renderNames() && !livingEntity.isInvisibleTo(mc.player));
        }
    }
}
