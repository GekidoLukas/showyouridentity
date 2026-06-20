package net.gekidolukas.showyouridentity;

import eu.midnightdust.lib.config.MidnightConfig;

public class SYIConfig extends MidnightConfig {
    private static final String CLIENT = "client";
    private static final String COMMON = "common";

    @Entry(category = CLIENT) public static boolean renderThirdPersonNameTag = true;
    @Entry(category = CLIENT, isSlider = true,min = 0.25, max = 2) public static float pronounScale = 0.5f;

    @Entry(category = COMMON) public static String slur_filter_url = "https://raw.githubusercontent.com/awdev1/better-profane-words/main/words.json";
    @Comment(category = COMMON, url = "https://github.com/awdev1/better-profane-words") public static Comment comment_slur_filter;
}
