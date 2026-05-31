package net.gekidolukas.showyouridentity.data;

import com.mojang.serialization.Codec;
import net.gekidolukas.showyouridentity.SYIMod;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;

public enum PrideFlag {

    NONE("", ""),
    RAINBOW("rainbow", "\uE000"),
    PROGRESS_PRIDE("progress_pride", "\uE001"),
    LESBIAN("lesbian", "\uE002"),
    GAY("gay", "\uE003"),
    BISEXUAL("bisexual", "\uE004"),
    TRANSGENDER("transgender", "\uE005"),
    INTERSEX("intersex", "\uE006"),
    ASEXUAL("asexual", "\uE007"),
    AROMANTIC("aromantic", "\uE008"),
    AGENDER("agender", "\uE009"),
    NON_BINARY("non_binary", "\uE010"),
    DEMIBOY("demiboy", "\uE011"),
    DEMIGIRL("demigirl", "\uE012"),
    GENDERFLUID("genderfluid", "\uE013"),
    GENDERQUEER("genderqueer", "\uE014"),
    DEMISEXUAL("demisexual", "\uE015"),
    DEMIROMANTIC("demiromantic", "\uE016"),
    PANSEXUAL("pansexual", "\uE017"),
    OMNISEXUAL("omnisexual", "\uE018"),
    POLYSEXUAL("polysexual", "\uE019"),
    POLYAMORY("polyamory", "\uE020"),
    BEAR_BROTHERHOOD("bear_brotherhood", "\uE021"),
    FEMBOY("femboy", "\uE022"),
    TRANSFEM("transfem", "\uE023"),
    TRANSMASC("transmasc", "\uE024"),
    NEPTUNIC("neptunic", "\uE025"),
    URANIC("uranic", "\uE026"),
    SAPPHIC("sapphic", "\uE027"),
    SAPIOSEXUAL("sapiosexual", "\uE028"),
    AROACE("aroace", "\uE029"),
    TRANS_ALLY("trans_ally", "\uF001"),
    STRAIGHT_ALLY("straight_ally", "\uF000");


    public static final FontDescription PRIDE_FONT = new FontDescription.Resource(SYIMod.id( "pride"));
    public static final Codec<PrideFlag> CODEC = Codec.STRING.xmap(
            PrideFlag::valueOf,
            Enum::name
    );

    private final String id;
    private final String unicode;

    PrideFlag(String id, String unicode) {
        this.id = id;
        this.unicode = unicode;
    }

    public String getUnicode() {
        return this.unicode;
    }

    public String getId() {
        return this.id;
    }

    public static PrideFlag byId(String id) {
        for (PrideFlag flag : values()) {
            if (flag.id.equalsIgnoreCase(id)) {
                return flag;
            }
        }
        return NONE;
    }

    public static boolean isKnownFlag(String id) {
        for (PrideFlag flag : values()) {
            if (flag.id.equalsIgnoreCase(id)) {
                return true;
            }
        }
        return false;
    }


    public String getFlagNameTranslationKey() {
        return "showyouridentity.flag." + this.id+".name";
    }

    public String getFlagDescriptionTranslationKey() {
        return "showyouridentity.flag." + this.id+".description";
    }

    public String getFlagCreditTranslationKey() {
        return "showyouridentity.flag." + this.id+".credit";
    }


    public static Component applyOverHeadFlags(Component originalName, PrideFlag leftFlag, PrideFlag rightFlag) {
        if (leftFlag == null || rightFlag == null ) {
            return originalName;
        }

        Component leftFlagComponent = Component.literal((leftFlag != PrideFlag.NONE ? leftFlag : rightFlag).getUnicode())
                .withStyle(style -> style.withFont(PRIDE_FONT));

        Component rightFlagComponent = Component.literal((rightFlag != PrideFlag.NONE ? rightFlag : leftFlag).getUnicode())
                .withStyle(style -> style.withFont(PRIDE_FONT));

        boolean noFlag = leftFlag == PrideFlag.NONE && rightFlag == PrideFlag.NONE;

        return leftFlagComponent.copy()
                .append(Component.literal(noFlag ? "" : " ").withStyle(style -> style.withFont(FontDescription.DEFAULT)))
                .append(originalName.copy().withStyle(style -> style.withFont(FontDescription.DEFAULT)))
                .append(Component.literal(noFlag ? "" : " ").withStyle(style -> style.withFont(FontDescription.DEFAULT)))
                .append(rightFlagComponent);
    }

    public static Component applyChatFlags(Component originalName, PrideFlag leftFlag, PrideFlag rightFlag) {
        if (leftFlag == null || rightFlag == null ) {
            return originalName;
        }

        Component leftFlagComponent = leftFlag != PrideFlag.NONE ?  Component.literal(leftFlag.getUnicode())
                .withStyle(style -> style.withFont(PRIDE_FONT)) : Component.empty();

        Component rightFlagComponent = rightFlag != PrideFlag.NONE ?  Component.literal( rightFlag.getUnicode())
                .withStyle(style -> style.withFont(PRIDE_FONT)) : Component.empty();


        return leftFlagComponent.copy()
                .append(rightFlagComponent)
                .append(originalName.copy().withStyle(style -> style.withFont(FontDescription.DEFAULT)));
    }
}
