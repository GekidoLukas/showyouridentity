package net.gekidolukas.showyouridentity.neoforge;

import net.gekidolukas.showyouridentity.data.IdentityData;
import net.gekidolukas.showyouridentity.neoforge.data.IdentitySavedData;
import net.minecraft.world.level.Level;

public class IdentityDataAccessorImpl {


    public static IdentityData getIdentityData(Level level) {

        return identitySavedData;
    }

    public static IdentitySavedData identitySavedData = new IdentitySavedData();
}
