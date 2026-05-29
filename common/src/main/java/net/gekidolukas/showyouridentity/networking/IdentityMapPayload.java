package net.gekidolukas.showyouridentity.networking;

import net.gekidolukas.showyouridentity.SYIMod;
import net.gekidolukas.showyouridentity.data.IdentityEntry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.Map;
import java.util.UUID;

public record IdentityMapPayload(Map<UUID, IdentityEntry> map) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<IdentityMapPayload> ID = new CustomPacketPayload.Type<>(SYIMod.id("identity_map"));

    public static final StreamCodec<FriendlyByteBuf, IdentityMapPayload> CODEC = StreamCodec.of(
            // Encoder
            (buf,payload) -> {
                buf.writeVarInt(payload.map.size());
                for (Map.Entry<UUID, IdentityEntry> entry : payload.map.entrySet()) {
                    buf.writeUUID(entry.getKey());
                    IdentityEntry.CODEC.encode(buf, entry.getValue());
                }
            },
            //Decoder
            buf -> {
                int size = buf.readVarInt();
                Map<UUID, IdentityEntry> map = new java.util.HashMap<>(size);

                for (int i = 0; i < size; i++) {
                    UUID key = buf.readUUID();
                    IdentityEntry value = IdentityEntry.CODEC.decode(buf);
                    map.put(key, value);
                }

                return new IdentityMapPayload(map);
            }
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }


}
