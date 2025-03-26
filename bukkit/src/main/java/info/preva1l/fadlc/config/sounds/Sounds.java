package info.preva1l.fadlc.config.sounds;

import de.exlll.configlib.*;
import info.preva1l.fadlc.Fadlc;
import info.preva1l.fadlc.config.AutoReload;
import info.preva1l.fadlc.utils.Logger;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Getter
@Configuration
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("FieldMayBeFinal")
public class Sounds {
    private static Sounds instance;

    private static final String FILE_NAME = "sounds.yml";
    private static final String CONFIG_HEADER = """
            ##########################################
            #                  Fadlc                 #
            #            Sound Configuration         #
            ##########################################
            """;

    private static final YamlConfigurationProperties PROPERTIES = YamlConfigurationProperties.newBuilder()
            .charset(StandardCharsets.UTF_8)
            .setNameFormatter(NameFormatters.LOWER_KEBAB_CASE)
            .header(CONFIG_HEADER).build();

    @Comment({"The sounds that can be used throughout the plugins configuration files.", "You can add as many as you want."})
    private Map<String, SoundType> sounds = Map.of(
            "click", new SoundType("minecraft:ui.button.click", 1.0f, 1.2f),
            "success", new SoundType("minecraft:block.note_block.bell", 1.0f, 1.2f),
            "fail", new SoundType("minecraft:block.note_block.didgeridoo", 1.0f, 0.5f),
            "example-custom", new SoundType("minecraft:ui.button.click", 0.5f, 2f)
    );

    public static SoundType getSound(String name) {
        return Sounds.i().sounds.get(name);
    }

    public static void playSound(Player player, SoundType soundType) {
        if (soundType == null) return;
        player.playSound(player.getLocation(), soundType.value(), SoundCategory.MASTER, soundType.volume(), soundType.pitch());
    }

    public static void reload() {
        instance = YamlConfigurations.load(new File(Fadlc.i().getDataFolder(), FILE_NAME).toPath(), Sounds.class, PROPERTIES);
        Logger.info("Configuration '%s' automatically reloaded from disk.".formatted(FILE_NAME));
    }

    public static Sounds i() {
        if (instance == null) {
            instance = YamlConfigurations.update(new File(Fadlc.i().getDataFolder(), FILE_NAME).toPath(), Sounds.class, PROPERTIES);
            AutoReload.watch(Fadlc.i().getDataFolder().toPath(), FILE_NAME, Sounds::reload);
        }

        return instance;
    }
}
