package net.gekidolukas.showyouridentity.client;

import dev.architectury.networking.NetworkManager;
import net.gekidolukas.showyouridentity.networking.IdentityMapPayload;

public class SYIModClient {


    public static void init() {

        NetworkManager.registerReceiver(
                NetworkManager.Side.S2C,
                IdentityMapPayload.ID,
                IdentityMapPayload.CODEC,
                SYIPacketHandler::handleIdentityMap
        );
    }
}
