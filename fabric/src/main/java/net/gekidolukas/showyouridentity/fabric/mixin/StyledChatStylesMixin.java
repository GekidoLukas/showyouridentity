package net.gekidolukas.showyouridentity.fabric.mixin;


import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.gekidolukas.showyouridentity.SYIConfig;
import net.gekidolukas.showyouridentity.util.PlayerNameModifier;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "eu.pb4.styledchat.StyledChatStyles")
public class StyledChatStylesMixin {

    @ModifyReturnValue(
            method = "getDisplayName",
            at = @At(value = "RETURN"))
    private static Component appendPronouns(Component originalName, ServerPlayer player, Component vanillaDisplayName) {
        return PlayerNameModifier.modifyName(originalName,player.getUUID(),player.level(), SYIConfig.renderServerSidePronounsInChat,SYIConfig.renderServerSideFlagsInChat, true);
    }
}
