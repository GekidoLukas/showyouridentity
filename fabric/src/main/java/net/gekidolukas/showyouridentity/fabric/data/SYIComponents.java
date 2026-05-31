package net.gekidolukas.showyouridentity.fabric.data;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentInitializer;
import net.gekidolukas.showyouridentity.SYIMod;
import net.minecraft.resources.ResourceLocation;

public class SYIComponents implements ScoreboardComponentInitializer {
    private static final ResourceLocation IDENTITY_DATA_ID = SYIMod.id("identity_data");
    public static final ComponentKey<IdentityComponent> IDENTITY_DATA = ComponentRegistryV3.INSTANCE.getOrCreate(IDENTITY_DATA_ID, IdentityComponent.class);
    @Override
    public void registerScoreboardComponentFactories(ScoreboardComponentFactoryRegistry scoreboardComponentFactoryRegistry) {
        scoreboardComponentFactoryRegistry.registerScoreboardComponent(IDENTITY_DATA, ((scoreboard, server) -> new IdentityComponent()));
    }
}
