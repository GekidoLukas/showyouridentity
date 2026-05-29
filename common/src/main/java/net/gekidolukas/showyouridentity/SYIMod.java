package net.gekidolukas.showyouridentity;

import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.EnvType;
import net.gekidolukas.showyouridentity.command.SYICommand;
import net.gekidolukas.showyouridentity.data.IdentityData;
import net.gekidolukas.showyouridentity.data.SlurFilter;
import net.gekidolukas.showyouridentity.networking.IdentityMapPayload;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SYIMod {
    public static final String MOD_ID = "showyouridentity";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static ResourceLocation id(String string) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID,string);
    }


    public static void init() {
        MidnightConfig.init(MOD_ID, SYIConfig.class);
        SlurFilter.initialize();

        if (Platform.getEnv() != EnvType.CLIENT) {
            NetworkManager.registerS2CPayloadType(IdentityMapPayload.ID, IdentityMapPayload.CODEC);
        }


        CommandRegistrationEvent.EVENT.register((dispatcher, registryAccess, selection) -> {
            SYICommand.register(dispatcher);
        });

        PlayerEvent.PLAYER_JOIN.register(serverPlayer -> {
            IdentityData identityData = IdentityData.get(serverPlayer.level());
            identityData.syncToPlayer(serverPlayer);
        });
    }

}
