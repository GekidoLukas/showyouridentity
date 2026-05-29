package net.gekidolukas.showyouridentity.data;

public enum NameFlagPos {
    PLAYER_NAME("player_name"),
    PRONOUNS("pronouns");


    private final String id;

    NameFlagPos(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public static NameFlagPos byId(String id) {
        for (NameFlagPos flag : values()) {
            if (flag.id.equalsIgnoreCase(id)) {
                return flag;
            }
        }
        return PLAYER_NAME;
    }

    public static boolean isKnownPos(String id) {
        for (NameFlagPos flag : values()) {
            if (flag.id.equalsIgnoreCase(id)) {
                return true;
            }
        }
        return false;
    }
}
