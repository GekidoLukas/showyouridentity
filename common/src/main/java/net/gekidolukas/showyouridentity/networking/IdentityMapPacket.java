package net.gekidolukas.showyouridentity.networking;

import io.netty.buffer.Unpooled;
import net.gekidolukas.showyouridentity.SYIMod;
import net.gekidolukas.showyouridentity.data.IdentityEntry;
import net.gekidolukas.showyouridentity.data.NameFlagPos;
import net.gekidolukas.showyouridentity.data.PrideFlag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.UUID;

public class IdentityMapPacket {
    public static final ResourceLocation ID = SYIMod.id("identity_map");

    private IdentityMapPacket() {
    }

    public static FriendlyByteBuf encode(Map<UUID, IdentityEntry> map) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());

        buf.writeVarInt(map.size());

        for (Map.Entry<UUID, IdentityEntry> entry : map.entrySet()) {
            buf.writeUUID(entry.getKey());
            buf.writeUtf(entry.getValue().getPronouns());
            buf.writeVarInt(entry.getValue().getPrimaryFlag().ordinal());
            buf.writeVarInt(entry.getValue().getSecondaryFlag().ordinal());
            buf.writeVarInt(entry.getValue().getFlagPos().ordinal());
        }

        return buf;
    }

    public static Map<UUID, IdentityEntry> decode(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        Map<UUID, IdentityEntry> map = new java.util.HashMap<>(size);

        for (int i = 0; i < size; i++) {
            UUID key = buf.readUUID();

            String pronounsEntry = buf.readUtf();
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

            map.put(key, new IdentityEntry(pronounsEntry, primaryFlag, secondaryFlag, flagPos));
        }

        return map;
    }
}
