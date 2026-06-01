package net.gekidolukas.showyouridentity.client;

import dev.architectury.networking.NetworkManager;
import net.gekidolukas.showyouridentity.data.IdentityData;
import net.gekidolukas.showyouridentity.networking.IdentityMapPayload;
import net.gekidolukas.showyouridentity.networking.ServerRenderOverridesPayload;
import net.minecraft.client.Minecraft;

public class SYIPacketHandler {


    public static void handleIdentityMap(IdentityMapPayload payload, NetworkManager.PacketContext context) {
        context.queue(() -> {
            Minecraft client = Minecraft.getInstance();
            if(!client.isSingleplayer()) {
                IdentityData.get(client.level).putMap(payload.map());
            }
        });
    }

    public static void handleServerOverrides(ServerRenderOverridesPayload payload, NetworkManager.PacketContext context) {
        context.queue(() -> {
            Minecraft client = Minecraft.getInstance();

            if(payload.overrideChat()) ClientToggles.shouldRenderChat = false;
            if(payload.overrideTab()) ClientToggles.shouldRenderTab = false;
            if(payload.overrideNameplate()) ClientToggles.shouldRenderNameplate = false;

        });
    }
}
