package net.gekidolukas.showyouridentity.fabric;

import net.gekidolukas.showyouridentity.data.IdentityData;
import net.gekidolukas.showyouridentity.fabric.data.SYIComponents;
import net.minecraft.world.level.Level;

public class IdentityDataAccessorImpl {

    public static IdentityData getIdentityData(Level level) {
        return SYIComponents.IDENTITY_DATA.get(level.getScoreboard());
    }
}
