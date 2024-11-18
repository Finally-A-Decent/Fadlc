package info.preva1l.fadlc.models.user;

import info.preva1l.fadlc.config.Lang;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface CommandUser {
    @NotNull
    Audience getAudience();

    boolean hasPermission(@NotNull String permission);

    default void sendMessage(@NotNull String message) {
        sendMessage(message, true);
    }

    default void sendMessage(@NotNull String message, boolean prefixed) {
        getAudience().sendMessage(MiniMessage.miniMessage().deserialize(Lang.i().getPrefix() + message));
    }

    default void sendMessage(@NotNull Component component) {
        getAudience().sendMessage(component);
    }

    Player asPlayer();
}