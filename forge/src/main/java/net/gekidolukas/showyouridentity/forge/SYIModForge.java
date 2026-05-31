package net.gekidolukas.showyouridentity.forge;

import net.gekidolukas.showyouridentity.SYIMod;
import net.gekidolukas.showyouridentity.forge.data.IdentitySavedData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(SYIMod.MOD_ID)
@Mod.EventBusSubscriber(modid = SYIMod.MOD_ID)
public final class SYIModForge {
    public SYIModForge() {
        SYIMod.init();
    }


    @SubscribeEvent
    public static void onServerStart(ServerStartedEvent event) {
        MinecraftServer server = event.getServer();
        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        assert overworld != null;
        DimensionDataStorage storage = overworld.getDataStorage();

        IdentityDataAccessorImpl.identitySavedData = storage.computeIfAbsent(IdentitySavedData::load, IdentitySavedData::new, "identity_data");
    }

}
