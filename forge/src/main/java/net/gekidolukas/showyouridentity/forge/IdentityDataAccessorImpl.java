package net.gekidolukas.showyouridentity.forge;

import net.gekidolukas.showyouridentity.data.IdentityData;
import net.gekidolukas.showyouridentity.forge.data.IdentitySavedData;
import net.minecraft.world.level.Level;

public class IdentityDataAccessorImpl {


    public static IdentityData getIdentityData(Level level) {

        return identitySavedData;
    }

    public static IdentitySavedData identitySavedData = new IdentitySavedData();
}
