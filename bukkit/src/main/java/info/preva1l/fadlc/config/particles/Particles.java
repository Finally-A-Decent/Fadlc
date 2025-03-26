package info.preva1l.fadlc.config.particles;

import de.exlll.configlib.Configuration;
import de.exlll.configlib.NameFormatters;
import de.exlll.configlib.YamlConfigurationProperties;
import de.exlll.configlib.YamlConfigurations;
import info.preva1l.fadlc.Fadlc;
import info.preva1l.fadlc.config.AutoReload;
import info.preva1l.fadlc.utils.Logger;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Getter
@Configuration
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("FieldMayBeFinal")
public class Particles {
    private static Particles instance;

    private static final String FILE_NAME = "particles.yml";
    private static final String CONFIG_HEADER = """
            ##########################################
            #                  Fadlc                 #
            #      Claim Borders Configuration       #
            ##########################################
            """;

    private static final YamlConfigurationProperties PROPERTIES = YamlConfigurationProperties.newBuilder()
            .charset(StandardCharsets.UTF_8)
            .setNameFormatter(NameFormatters.LOWER_KEBAB_CASE)
            .header(CONFIG_HEADER).build();

    private Map<String, ParticleType> particles = Map.of(

    );

    public static ParticleType getParticle(String name) {
        ParticleType type = Particles.i().particles.get(name);
        if (type == null) {
            type = Particles.i().particles.values().stream().findFirst()
                    .orElse(new ParticleType(
                            "Fix Your Config",
                            new ParticleType.Description(
                                    List.of("issue"), List.of("errm")
                            ),
                            "fadlc.particle.default",
                            "VILLAGER_HAPPY",
                            new ParticleType.ParticleColor(255, 0, 0),
                            1,
                            1
                    ));
        }
        return type;
    }

    public static void showParticle(Player player, String border, Location location) {
        ParticleType particleType = getParticle(border);

        Particle bukkit;
        try {
            bukkit = Particle.valueOf(particleType.value());
        } catch (IllegalArgumentException e) {
            bukkit = Particle.VILLAGER_HAPPY;
        }

        if (bukkit == Particle.REDSTONE) {
            Particle.DustOptions options = new Particle.DustOptions(particleType.color().toBukkit(), particleType.size());
            player.spawnParticle(bukkit, location, particleType.amount(), options);
            return;
        }
        player.spawnParticle(bukkit, location, particleType.amount(), 0, 0, 0);
    }

    public static void reload() {
        instance = YamlConfigurations.load(new File(Fadlc.i().getDataFolder(), FILE_NAME).toPath(), Particles.class, PROPERTIES);
        Logger.info("Configuration '%s' automatically reloaded from disk.".formatted(FILE_NAME));
    }

    public static Particles i() {
        if (instance == null) {
            instance = YamlConfigurations.update(new File(Fadlc.i().getDataFolder(), FILE_NAME).toPath(), Particles.class, PROPERTIES);
            AutoReload.watch(Fadlc.i().getDataFolder().toPath(), FILE_NAME, Particles::reload);
        }

        return instance;
    }
}
