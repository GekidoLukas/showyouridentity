package net.gekidolukas.showyouridentity.neoforge;

import net.gekidolukas.showyouridentity.neoforge.data.IdentitySavedData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;

import net.gekidolukas.showyouridentity.SYIMod;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

@Mod(SYIMod.MOD_ID)
@EventBusSubscriber(modid = SYIMod.MOD_ID)
public final class SYIModNeoForge {
    public SYIModNeoForge() {
        SYIMod.init();



        IEventBus modBus = ModLoadingContext.get().getActiveContainer().getEventBus();

    }


    @SubscribeEvent
    private static void onServerStart(ServerStartedEvent event) {
        MinecraftServer server = event.getServer();
        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        assert overworld != null;
        DimensionDataStorage storage = overworld.getDataStorage();

        IdentityDataAccessorImpl.identitySavedData = storage.computeIfAbsent(IdentitySavedData.FACTORY,"identity_data");
    }

}
