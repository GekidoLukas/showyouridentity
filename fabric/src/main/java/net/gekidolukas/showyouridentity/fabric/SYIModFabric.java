package net.gekidolukas.showyouridentity.fabric;

import net.fabricmc.api.ModInitializer;

import net.gekidolukas.showyouridentity.SYIMod;

public final class SYIModFabric implements ModInitializer {
    @Override
    public void onInitialize() {

        SYIMod.init();

    }
}
