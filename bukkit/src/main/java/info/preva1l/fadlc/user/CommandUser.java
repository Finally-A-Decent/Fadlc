package info.preva1l.fadlc.user;

import info.preva1l.fadlc.models.Replacer;
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface CommandUser {
    @NotNull Audience getAudience();

    boolean hasPermission(@NotNull String permission);

    default void sendMessage(String message, Replacer... replacements) {
        sendMessage(message, true, replacements);
    }

    void sendMessage(String message, boolean prefixed, Replacer... replacements);

    Player asPlayer();
}