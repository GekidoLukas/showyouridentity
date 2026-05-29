package net.gekidolukas.showyouridentity.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.gekidolukas.showyouridentity.client.SYIModClient;

public final class SYIModFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SYIModClient.init();
    }
}
