package net.gekidolukas.showyouridentity.neoforge.data;

import com.mojang.serialization.Codec;
import dev.architectury.networking.NetworkManager;
import net.gekidolukas.showyouridentity.SYIMod;
import net.gekidolukas.showyouridentity.data.IdentityData;
import net.gekidolukas.showyouridentity.data.IdentityEntry;
import net.gekidolukas.showyouridentity.neoforge.IdentityDataAccessorImpl;
import net.gekidolukas.showyouridentity.networking.IdentityMapPayload;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class IdentitySavedData extends SavedData implements IdentityData {
    public static final Codec<IdentitySavedData> CODEC = IdentityData.CODEC.xmap(
            IdentitySavedData::new,
            data -> data.IDENTITIES
    );

    Map<UUID, IdentityEntry> IDENTITIES;

    public IdentitySavedData() {
        IDENTITIES = new HashMap<>();
    }

    public IdentitySavedData(Map<UUID, IdentityEntry> identities) {
        this.IDENTITIES = new HashMap<>(identities);
    }

    public static final SavedDataType<IdentitySavedData> TYPE = new SavedDataType<>(
            "identity_data",
            IdentitySavedData::new,
            CODEC
    );

    @Override
    public IdentityEntry getIdentity(Player player) {
        if(player == null) return null;
        return IDENTITIES.get(player.getUUID());
    }

    @Override
    public void putIdentity(@NotNull Player player, @NotNull IdentityEntry entry) {
        IDENTITIES.put(player.getUUID(),entry);
        markNeoDirty();
    }

    @Override
    public void removeIdentity(@NotNull Player player) {
        IDENTITIES.remove(player.getUUID());
        markNeoDirty();
    }

    @Override
    public void putMap(Map<UUID, IdentityEntry> map) {
        IDENTITIES.clear();
        IDENTITIES.putAll(map);
        markNeoDirty();
    }

    @Override
    public void reset() {
        IDENTITIES.clear();
        markNeoDirty();
    }

    @Override
    public void markNeoDirty() {
        IdentityDataAccessorImpl.identitySavedData.setDirty(true);
    }

    @Override
    public void sync(Level level) {
        if(level instanceof ServerLevel serverLevel) {
            NetworkManager.sendToPlayers(serverLevel.getServer().getPlayerList().getPlayers(), new IdentityMapPayload(IDENTITIES));

        }
    }

    @Override
    public void syncToPlayer(ServerPlayer serverPlayer) {
        NetworkManager.sendToPlayer(serverPlayer, new IdentityMapPayload(IDENTITIES));
    }
}
