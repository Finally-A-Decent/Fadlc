package info.preva1l.fadlc.config.sounds;

import org.bukkit.entity.Player;

public record SoundType(
    String value,
    float volume,
    float pitch
) {
    public void play(Player player) {
        Sounds.playSound(player, this);
    }
}
