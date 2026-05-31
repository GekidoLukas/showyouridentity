package net.gekidolukas.showyouridentity.client;

import dev.architectury.networking.NetworkManager;
import net.gekidolukas.showyouridentity.data.IdentityData;
import net.gekidolukas.showyouridentity.data.IdentityEntry;
import net.gekidolukas.showyouridentity.networking.IdentityMapPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Map;
import java.util.UUID;

public class SYIPacketHandler {
    public static void handleIdentityMap(FriendlyByteBuf buf, NetworkManager.PacketContext context) {
        Map<UUID, IdentityEntry> map = IdentityMapPacket.decode(buf);
        context.queue(() -> {
            Minecraft client = Minecraft.getInstance();
            if(!client.isSingleplayer()) {
                IdentityData.get(client.level).putMap(map);
            }
        });
    }
}
