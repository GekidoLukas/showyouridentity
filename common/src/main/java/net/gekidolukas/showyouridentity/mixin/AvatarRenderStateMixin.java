package net.gekidolukas.showyouridentity.mixin;

import net.gekidolukas.showyouridentity.client.AvatarRenderStateExtension;
import net.gekidolukas.showyouridentity.data.IdentityEntry;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AvatarRenderState.class)
public class AvatarRenderStateMixin implements AvatarRenderStateExtension {
    @Unique
    private IdentityEntry showyouridentity$identity;

    @Override
    public @Nullable IdentityEntry showyouridentity$getIdentity() {
        return showyouridentity$identity;
    }

    @Override
    public void showyouridentity$setIdentity(@Nullable IdentityEntry entry) {
        showyouridentity$identity = entry;
    }
}
