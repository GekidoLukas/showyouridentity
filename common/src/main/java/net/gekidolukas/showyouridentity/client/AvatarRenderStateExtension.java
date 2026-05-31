package net.gekidolukas.showyouridentity.client;

import net.gekidolukas.showyouridentity.data.IdentityEntry;
import org.jetbrains.annotations.Nullable;

public interface AvatarRenderStateExtension {
    @Nullable IdentityEntry showyouridentity$getIdentity();

    void showyouridentity$setIdentity(@Nullable IdentityEntry entry);
}
