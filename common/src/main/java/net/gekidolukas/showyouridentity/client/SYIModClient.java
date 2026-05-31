package net.gekidolukas.showyouridentity.client;

import dev.architectury.networking.NetworkManager;
import net.gekidolukas.showyouridentity.networking.IdentityMapPacket;

public class SYIModClient {


    public static void init() {
        NetworkManager.registerReceiver(
                NetworkManager.Side.S2C,
                IdentityMapPacket.ID,
                SYIPacketHandler::handleIdentityMap
        );
    }
}
