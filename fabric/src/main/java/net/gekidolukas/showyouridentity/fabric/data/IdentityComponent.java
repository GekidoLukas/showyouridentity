package net.gekidolukas.showyouridentity.fabric.data;

import dev.architectury.networking.NetworkManager;
import net.gekidolukas.showyouridentity.data.NameFlagPos;
import net.gekidolukas.showyouridentity.data.PrideFlag;
import net.gekidolukas.showyouridentity.data.IdentityData;
import net.gekidolukas.showyouridentity.data.IdentityEntry;
import net.gekidolukas.showyouridentity.networking.IdentityMapPayload;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.component.ComponentV3;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class IdentityComponent implements IdentityData, ComponentV3, AutoSyncedComponent {

    Map<UUID, IdentityEntry> IDENTITIES;

    public IdentityComponent() {
        IDENTITIES = new HashMap<>();
    }

    @Override
    public void readData(ValueInput readView) {
        readView.read("identities", IdentityData.CODEC).ifPresent(identities -> IDENTITIES = new HashMap<>(identities));
    }

    @Override
    public void writeData(ValueOutput writeView) {
        writeView.store("identities", IdentityData.CODEC, IDENTITIES);
    }

    @Override
    public IdentityEntry getIdentity(Player player) {
        if(player == null) return null;
        return IDENTITIES.get(player.getUUID());
    }

    @Override
    public void putIdentity(@NotNull Player player, @NotNull IdentityEntry entry) {
        IDENTITIES.put(player.getUUID(),entry);
    }

    @Override
    public void removeIdentity(@NotNull Player player) {
        IDENTITIES.remove(player.getUUID());
    }

    @Override
    public void putMap(Map<UUID, IdentityEntry> map) {

    }

    @Override
    public void reset() {
        IDENTITIES.clear();
    }

    @Override
    public void markNeoDirty() {
        //Unused
    }

    @Override
    public void sync(Level level) {
        SYIComponents.IDENTITY_DATA.sync(level.getScoreboard());
    }

    @Override
    public void syncToPlayer(ServerPlayer serverPlayer) {
        NetworkManager.sendToPlayer(serverPlayer, new IdentityMapPayload(IDENTITIES));
    }

    @Override
    public void applySyncPacket(RegistryFriendlyByteBuf buf) {
        IDENTITIES.clear();
        int size = buf.readVarInt();
        for (int i = 0; i < size; i++) {
            UUID uuid = buf.readUUID();
            String pronouns = buf.readUtf(32767);

            int indexPrimary = buf.readVarInt();
            int indexSecondary = buf.readVarInt();
            int indexFlagPos = buf.readVarInt();
            PrideFlag primaryFlag;
            PrideFlag secondaryFlag;
            NameFlagPos flagPos;
            try {
                primaryFlag = PrideFlag.values()[indexPrimary];
                secondaryFlag = PrideFlag.values()[indexSecondary];
                flagPos = NameFlagPos.values()[indexFlagPos];
            } catch (Exception ignored) {
                primaryFlag = PrideFlag.NONE;
                secondaryFlag = PrideFlag.NONE;
                flagPos = NameFlagPos.PLAYER_NAME;
            }

            IDENTITIES.put(uuid, new IdentityEntry(pronouns, primaryFlag,secondaryFlag,flagPos));
        }
    }

    @Override
    public void writeSyncPacket(RegistryFriendlyByteBuf buf, ServerPlayer recipient) {
        buf.writeVarInt(IDENTITIES.size());
        for (Map.Entry<UUID, IdentityEntry> entry : IDENTITIES.entrySet()) {
            buf.writeUUID(entry.getKey());
            buf.writeUtf(entry.getValue().getPronouns());
            buf.writeVarInt(entry.getValue().getPrimaryFlag().ordinal());
            buf.writeVarInt(entry.getValue().getSecondaryFlag().ordinal());
            buf.writeVarInt(entry.getValue().getFlagPos().ordinal());
        }
    }
}
