package info.preva1l.fadlc.models.user;

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
    public Player asPlayer() {
        throw new IllegalStateException("ConsoleUser is not a player");
    }
}