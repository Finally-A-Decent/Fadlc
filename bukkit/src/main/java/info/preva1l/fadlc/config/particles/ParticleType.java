package info.preva1l.fadlc.config.particles;

import org.bukkit.Color;

import java.util.List;

public record ParticleType(
    String display,
    Description description,
    String permission,
    String value,
    ParticleColor color,
    int amount,
    float size
) {
    public record ParticleColor(
            int red,
            int green,
            int blue
    ) {
        public Color toBukkit() {
            return Color.fromRGB(red, green, blue);
        }
    }

    public record Description(
            List<String> unlocked,
            List<String> locked
    ) {}
}
