package net.gekidolukas.showyouridentity.fabric.data;

import net.gekidolukas.showyouridentity.SYIMod;
import net.minecraft.resources.ResourceLocation;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistryV3;
import org.ladysnake.cca.api.v3.scoreboard.ScoreboardComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.scoreboard.ScoreboardComponentInitializer;

public class SYIComponents implements ScoreboardComponentInitializer {
    private static final ResourceLocation IDENTITY_DATA_ID = ResourceLocation.fromNamespaceAndPath(SYIMod.MOD_ID, "identity_data");
    public static final ComponentKey<IdentityComponent> IDENTITY_DATA = ComponentRegistryV3.INSTANCE.getOrCreate(IDENTITY_DATA_ID, IdentityComponent.class);
    @Override
    public void registerScoreboardComponentFactories(ScoreboardComponentFactoryRegistry scoreboardComponentFactoryRegistry) {
        scoreboardComponentFactoryRegistry.registerScoreboardComponent(IDENTITY_DATA, ((scoreboard, server) -> new IdentityComponent()));
    }
}
