package info.preva1l.fadlc.config.sounds;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Sound;

@Getter
@AllArgsConstructor
public class SoundType {
    private final String name;
    private final String bukkit;
    private final float volume;
    private final float pitch;
}
