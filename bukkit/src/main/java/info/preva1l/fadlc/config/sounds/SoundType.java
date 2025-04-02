package info.preva1l.fadlc.config.sounds;

import info.preva1l.fadlc.models.user.OnlineUser;
import org.bukkit.entity.Player;

public record SoundType(
    String value,
    float volume,
    float pitch
) {
    public void play(OnlineUser user) {
        play(user.asPlayer());
    }

    public void play(Player player) {
        Sounds.playSound(player, this);
    }
}
