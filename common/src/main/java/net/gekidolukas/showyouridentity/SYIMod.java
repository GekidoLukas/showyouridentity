package net.gekidolukas.showyouridentity;

import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.PlayerEvent;
import eu.midnightdust.lib.config.MidnightConfig;
import net.gekidolukas.showyouridentity.command.SYICommand;
import net.gekidolukas.showyouridentity.data.IdentityData;
import net.gekidolukas.showyouridentity.data.SlurFilter;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SYIMod {
    public static final String MOD_ID = "showyouridentity";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static ResourceLocation id(String string) {
        return new ResourceLocation(MOD_ID,string);
    }


    public static void init() {
        MidnightConfig.init(MOD_ID, SYIConfig.class);
        SlurFilter.initialize();

        CommandRegistrationEvent.EVENT.register((dispatcher, registryAccess, selection) -> {
            SYICommand.register(dispatcher);
        });

        PlayerEvent.PLAYER_JOIN.register(serverPlayer -> {
            System.out.println("sync to " + serverPlayer);
            IdentityData identityData = IdentityData.get(serverPlayer.level());
            identityData.syncToPlayer(serverPlayer);
        });
    }

}
