package info.preva1l.fadlc.models.user;

import info.preva1l.fadlc.config.Lang;
import info.preva1l.fadlc.utils.Text;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface CommandUser {
    @NotNull Audience getAudience();

    boolean hasPermission(@NotNull String permission);

    default void sendMessage(@NotNull String message) {
        sendMessage(message, true);
    }

    default void sendMessage(@NotNull String message, boolean prefixed) {
        getAudience().sendMessage(Text.text((prefixed ? Lang.i().getPrefix() : "") + message));
    }

    default void sendMessage(@NotNull Component message) {
        getAudience().sendMessage(message);
    }

    Player asPlayer();
}