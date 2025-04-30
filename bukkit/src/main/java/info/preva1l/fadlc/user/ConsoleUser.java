package info.preva1l.fadlc.user;

import info.preva1l.fadlc.config.Lang;
import info.preva1l.fadlc.models.Replacer;
import info.preva1l.fadlc.utils.Text;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class ConsoleUser implements CommandUser {
    public static final ConsoleUser SELF = new ConsoleUser();

    private ConsoleUser() {}

    private final Audience audience = Bukkit.getConsoleSender();

    @Override
    public @NotNull Audience getAudience() {
        return audience;
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return true;
    }

    @Override
    public void sendMessage(String message, boolean prefixed, Replacer... replacements) {
        getAudience().sendMessage(Text.text((prefixed ? Lang.i().getPrefix() : "") + message));
    }

    @Override
    public Player asPlayer() {
        throw new IllegalStateException("ConsoleUser is not a player");
    }
}