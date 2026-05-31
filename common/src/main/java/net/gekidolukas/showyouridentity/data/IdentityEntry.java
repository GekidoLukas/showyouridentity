package net.gekidolukas.showyouridentity.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.storage.ValueInput;

public class IdentityEntry {

    private String pronouns = "";

    private PrideFlag primaryFlag = PrideFlag.NONE;
    private PrideFlag secondaryFlag = PrideFlag.NONE;
    private NameFlagPos flagPos = NameFlagPos.PLAYER_NAME;

    public IdentityEntry(String pronouns, PrideFlag primaryFlag, PrideFlag secondaryFlag, NameFlagPos flagPos) {
        this.pronouns = pronouns;
        this.primaryFlag = primaryFlag;
        this.secondaryFlag = secondaryFlag;
        this.flagPos = flagPos;
    }


    public String getPronouns() {
        return pronouns;
    }

    public void setPronouns(String pronouns) {
        this.pronouns = pronouns;
    }

    public PrideFlag getPrimaryFlag() {
        return primaryFlag;
    }

    public PrideFlag getSecondaryFlag() {
        return secondaryFlag;
    }

    public void setPrimaryFlag(PrideFlag primaryFlag) {
        this.primaryFlag = primaryFlag;
    }

    public void setSecondaryFlag(PrideFlag secondaryFlag) {
        this.secondaryFlag = secondaryFlag;
    }

    public NameFlagPos getFlagPos() {
        return flagPos;
    }

    public void setFlagPos(NameFlagPos flagPos) {
        this.flagPos = flagPos;
    }

    public static IdentityEntry fromNBT(CompoundTag tag) {
        String pronouns = tag.getString("pronouns").orElse("");
        PrideFlag primaryFlag = PrideFlag.byId(tag.getString("primary_flag").orElse("none"));
        PrideFlag secondaryFlag = PrideFlag.byId(tag.getString("secondary_flag").orElse("none"));
        NameFlagPos flagPos = NameFlagPos.byId(tag.getString("flag_pos").orElse("player_name"));

        return new IdentityEntry(pronouns,primaryFlag,secondaryFlag,flagPos);
    }

    public static IdentityEntry fromValueInput(ValueInput readView) {
        String pronouns = readView.getStringOr("pronouns", "");
        PrideFlag primaryFlag = PrideFlag.byId(readView.getStringOr("primary_flag", "none"));
        PrideFlag secondaryFlag = PrideFlag.byId(readView.getStringOr("secondary_flag", "none"));
        NameFlagPos flagPos = NameFlagPos.byId(readView.getStringOr("flag_pos", "player_name"));

        return new IdentityEntry(pronouns,primaryFlag,secondaryFlag,flagPos);
    }

    public CompoundTag toNBT() {
        CompoundTag nbt = new CompoundTag();

        nbt.putString("pronouns",this.getPronouns());
        nbt.putString("primary_flag",this.getPrimaryFlag().getId());
        nbt.putString("secondary_flag",this.getSecondaryFlag().getId());
        nbt.putString("flag_pos",this.getFlagPos().getId());
        return nbt;
    }

    public static final Codec<IdentityEntry> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.optionalFieldOf("pronouns", "")
                            .forGetter(IdentityEntry::getPronouns),
                    PrideFlag.CODEC.optionalFieldOf("primary_flag", PrideFlag.NONE)
                            .forGetter(IdentityEntry::getPrimaryFlag),
                    PrideFlag.CODEC.optionalFieldOf("secondary_flag", PrideFlag.NONE)
                            .forGetter(IdentityEntry::getSecondaryFlag),
                    NameFlagPos.CODEC.optionalFieldOf("flag_pos", NameFlagPos.PLAYER_NAME)
                            .forGetter(IdentityEntry::getFlagPos)
            ).apply(instance, IdentityEntry::new)
    );
    public static final StreamCodec<FriendlyByteBuf, IdentityEntry> STREAM_CODEC = StreamCodec.of(
            (buf,identityEntry) -> {
                buf.writeUtf(identityEntry.pronouns);
                buf.writeVarInt(identityEntry.primaryFlag.ordinal());
                buf.writeVarInt(identityEntry.secondaryFlag.ordinal());
                buf.writeVarInt(identityEntry.flagPos.ordinal());

            },
            buf -> {
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


                return new IdentityEntry(pronounsEntry, primaryFlag,secondaryFlag,flagPos);
            }
    );
}
