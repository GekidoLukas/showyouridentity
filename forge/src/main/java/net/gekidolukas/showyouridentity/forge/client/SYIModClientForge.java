package net.gekidolukas.showyouridentity.forge.client;

import net.gekidolukas.showyouridentity.SYIMod;
import net.gekidolukas.showyouridentity.client.SYIModClient;
import net.gekidolukas.showyouridentity.forge.IdentityDataAccessorImpl;
import net.gekidolukas.showyouridentity.forge.data.IdentitySavedData;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = SYIMod.MOD_ID, value = Dist.CLIENT)
public class SYIModClientForge {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
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
