package net.gekidolukas.showyouridentity.server;

import dev.architectury.platform.Platform;
import net.gekidolukas.showyouridentity.networking.ServerRenderOverridesPayload;

public class ServerOverrideValidator {


    public static ServerRenderOverridesPayload getValidationPayload() {
        boolean overrideChat = false;
        boolean overrideTab = false;
        boolean overrideNameplate = false;

        if(Platform.isModLoaded("styledchat")) {
            overrideChat = true;
        }

        return new ServerRenderOverridesPayload(overrideChat, overrideTab,overrideNameplate);

    }
}
