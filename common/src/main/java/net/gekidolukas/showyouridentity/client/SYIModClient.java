package net.gekidolukas.showyouridentity.client;

import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.networking.NetworkManager;
import net.gekidolukas.showyouridentity.SYIConfig;
import net.gekidolukas.showyouridentity.networking.IdentityMapPayload;
import net.gekidolukas.showyouridentity.networking.ServerRenderOverridesPayload;

public class SYIModClient {


    public static void init() {

        NetworkManager.registerReceiver(
                NetworkManager.Side.S2C,
                IdentityMapPayload.ID,
                IdentityMapPayload.CODEC,
                SYIPacketHandler::handleIdentityMap
        );
        NetworkManager.registerReceiver(
                NetworkManager.Side.S2C,
                ServerRenderOverridesPayload.ID,
                ServerRenderOverridesPayload.CODEC,
                SYIPacketHandler::handleServerOverrides
        );

        ClientPlayerEvent.CLIENT_PLAYER_QUIT.register(localPlayer -> {
            ClientToggles.shouldRenderChat = true;
            ClientToggles.shouldRenderTab = true;
            ClientToggles.shouldRenderNameplate = true;
        });
    }
}
