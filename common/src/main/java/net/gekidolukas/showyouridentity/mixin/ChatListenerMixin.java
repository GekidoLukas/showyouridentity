package net.gekidolukas.showyouridentity.mixin;

import com.mojang.authlib.GameProfile;
import net.gekidolukas.showyouridentity.SYIConfig;
import net.gekidolukas.showyouridentity.client.ClientToggles;
import net.gekidolukas.showyouridentity.util.PlayerNameModifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.chat.ChatListener;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(value = ChatListener.class, priority = 100)
public class ChatListenerMixin {

    @Unique
    private static final ThreadLocal<GameProfile> CURRENT_CHAT_PROFILE = new ThreadLocal<>();

    @Inject(method = "handlePlayerChatMessage", at = @At("HEAD"))
    private void captureSenderProfile(PlayerChatMessage playerChatMessage, GameProfile profile, ChatType.Bound bound, CallbackInfo ci) {
        CURRENT_CHAT_PROFILE.set(profile);
    }

    @ModifyVariable(
            method = "handlePlayerChatMessage",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private ChatType.Bound modifyBound(ChatType.Bound bound) {
        GameProfile profile = CURRENT_CHAT_PROFILE.get();
        if(profile != null) {
            Component newName = PlayerNameModifier.modifyName(bound.name(),profile.getId(),Minecraft.getInstance().level, SYIConfig.renderPronounsInChat,SYIConfig.renderFlagsInChat, ClientToggles.shouldRenderChat);
            if(newName != bound.name()) {
                return new ChatType.Bound(bound.chatType(), newName, bound.targetName());
            }
        }
        return bound;
    }

    @Inject(method = "handlePlayerChatMessage", at = @At("RETURN"))
    private void clearSenderProfile(PlayerChatMessage playerChatMessage, GameProfile gameProfile, ChatType.Bound bound, CallbackInfo ci) {
        CURRENT_CHAT_PROFILE.remove();
    }
}
