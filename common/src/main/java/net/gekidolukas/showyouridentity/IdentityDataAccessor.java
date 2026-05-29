package net.gekidolukas.showyouridentity;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.gekidolukas.showyouridentity.data.IdentityData;
import net.minecraft.world.level.Level;

public class IdentityDataAccessor {



    @ExpectPlatform
    public static IdentityData getIdentityData(Level level) {
        throw new AssertionError();
    }


}
