package net.gekidolukas.showyouridentity.neoforge.client;

import net.gekidolukas.showyouridentity.SYIMod;
import net.gekidolukas.showyouridentity.client.SYIModClient;
import net.gekidolukas.showyouridentity.neoforge.IdentityDataAccessorImpl;
import net.gekidolukas.showyouridentity.neoforge.data.IdentitySavedData;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

@EventBusSubscriber(modid = SYIMod.MOD_ID, value = Dist.CLIENT)
public class SYIModClientNeoForge {

    @SubscribeEvent
    private static void onClientSetup(FMLClientSetupEvent event) {
        SYIModClient.init();
    }


    @SubscribeEvent
    public static void onClientJoin(ClientPlayerNetworkEvent.LoggingIn event) {
        if(!Minecraft.getInstance().hasSingleplayerServer()){
            IdentityDataAccessorImpl.identitySavedData = new IdentitySavedData();
        }
    }

    @SubscribeEvent
    public static void onClientLeave(ClientPlayerNetworkEvent.LoggingOut event) {
        if(!Minecraft.getInstance().hasSingleplayerServer()){
            IdentityDataAccessorImpl.identitySavedData = null;
        }

    }

}
