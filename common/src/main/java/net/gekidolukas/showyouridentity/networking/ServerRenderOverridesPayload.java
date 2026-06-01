package net.gekidolukas.showyouridentity.networking;

import net.gekidolukas.showyouridentity.SYIMod;
import net.gekidolukas.showyouridentity.data.IdentityEntry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.Map;
import java.util.UUID;

public record ServerRenderOverridesPayload(boolean overrideChat, boolean overrideTab, boolean overrideNameplate) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ServerRenderOverridesPayload> ID = new CustomPacketPayload.Type<>(SYIMod.id("server_overrides"));

    public static final StreamCodec<FriendlyByteBuf, ServerRenderOverridesPayload> CODEC = StreamCodec.of(
            // Encoder
            (buf,payload) -> {
                buf.writeBoolean(payload.overrideChat);
                buf.writeBoolean(payload.overrideTab);
                buf.writeBoolean(payload.overrideNameplate);

            },
            //Decoder
            buf -> {
                boolean overrideChat = buf.readBoolean();
                boolean overrideTab = buf.readBoolean();
                boolean overrideNameplate = buf.readBoolean();

                return new ServerRenderOverridesPayload(overrideChat,overrideTab,overrideNameplate);
            }
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
