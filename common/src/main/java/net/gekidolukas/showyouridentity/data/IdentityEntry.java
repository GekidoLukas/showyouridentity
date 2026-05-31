package net.gekidolukas.showyouridentity.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

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
        String pronouns = tag.contains("pronouns") ? tag.getString("pronouns") : "";
        PrideFlag primaryFlag = PrideFlag.byId(tag.contains("primary_flag") ? tag.getString("primary_flag") : "none");
        PrideFlag secondaryFlag = PrideFlag.byId(tag.contains("secondary_flag") ? tag.getString("secondary_flag") : "none");
        NameFlagPos flagPos = NameFlagPos.byId(tag.contains("flag_pos") ? tag.getString("flag_pos") : "player_name");


        IdentityEntry entry = new IdentityEntry(pronouns,primaryFlag,secondaryFlag,flagPos);

        return entry;
    }

    public CompoundTag toNBT() {
        CompoundTag nbt = new CompoundTag();

        nbt.putString("pronouns",this.getPronouns());
        nbt.putString("primary_flag",this.getPrimaryFlag().getId());
        nbt.putString("secondary_flag",this.getSecondaryFlag().getId());
        nbt.putString("flag_pos",this.getFlagPos().getId());
        return nbt;
    }
}
