package net.gekidolukas.showyouridentity.forge.data;

import dev.architectury.networking.NetworkManager;
import net.gekidolukas.showyouridentity.data.IdentityData;
import net.gekidolukas.showyouridentity.data.IdentityEntry;
import net.gekidolukas.showyouridentity.forge.IdentityDataAccessorImpl;
import net.gekidolukas.showyouridentity.networking.IdentityMapPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class IdentitySavedData extends SavedData implements IdentityData {


    Map<UUID, IdentityEntry> IDENTITIES;


    public IdentitySavedData() {
        IDENTITIES = new HashMap<>();
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag tag) {
        tag.put("identities",  IdentityData.mapToNbt(IDENTITIES));

        return tag;
    }

    public static IdentitySavedData load(CompoundTag tag) {
        IdentitySavedData data = new IdentitySavedData();
        if(tag.contains("identities")) {
            data.IDENTITIES = IdentityData.nbtToMap(tag.getCompound("identities"));
        }
        return data;
    }

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
            NetworkManager.sendToPlayers(serverLevel.getServer().getPlayerList().getPlayers(), IdentityMapPacket.ID, IdentityMapPacket.encode(IDENTITIES));

        }
    }

    @Override
    public void syncToPlayer(ServerPlayer serverPlayer) {
        NetworkManager.sendToPlayer(serverPlayer, IdentityMapPacket.ID, IdentityMapPacket.encode(IDENTITIES));
    }
}
