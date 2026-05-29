package net.gekidolukas.showyouridentity.client;

import dev.architectury.networking.NetworkManager;
import net.gekidolukas.showyouridentity.data.IdentityData;
import net.gekidolukas.showyouridentity.networking.IdentityMapPayload;
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
}
