package info.preva1l.fadlc.config.menus;

import de.exlll.configlib.*;
import info.preva1l.fadlc.Fadlc;
import info.preva1l.fadlc.config.AutoReload;
import info.preva1l.fadlc.config.misc.ConfigurableItem;
import info.preva1l.fadlc.config.sounds.SoundType;
import info.preva1l.fadlc.config.sounds.Sounds;
import info.preva1l.fadlc.utils.Logger;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Material;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Getter
@Configuration
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("FieldMayBeFinal")
public class ProfilesConfig implements MenuConfig {
    private static ProfilesConfig instance;
    private static final YamlConfigurationProperties PROPERTIES = YamlConfigurationProperties.newBuilder()
            .charset(StandardCharsets.UTF_8)
            .setNameFormatter(NameFormatters.LOWER_KEBAB_CASE).build();

    private String title = "&8Manage Profiles";

    private Lang lang = new Lang();

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Lang {
        private ConfigurableItem back = new ConfigurableItem(
                Material.FEATHER, 0, "click",
                "&3Buy Chunks", List.of("&fYou have &3%chunks% &fchunks", "&7Click to purchase more")
        );
        private ConfigurableItem switchProfile = new ConfigurableItem(
                Material.PAPER, 0, "click", "&3Switch Profile",
                List.of(
                        "&7→ Left Click to cycle up",
                        "&7→ Right Click to cycle down",
                        "&8-------------------------",
                        "&f%previous%",
                        "&8> &3%current%",
                        "&f%next%",
                        "&8-------------------------"
                )
        );
        private Profile profile = new Profile(
                "click", "%profile%",
                List.of(
                        "&3Info:",
                        "&7‣ &3Members: &f%members%",
                        "&7‣ &3Border: &f%border%",
                        "&7‣ &3Chunks: &f%chunks%",
                        "&3Flags:",
                        "%flags%"
                ), new Flag("&aEnabled", "&cDisabled", "&7‣ &3%flag%: &r%status%")
        );
        public record Profile(String sound, String name, List<String> lore, Flag flag) {
            public SoundType getSound() {
                return Sounds.getSound(sound);
            }
        }
        public record Flag(String enabled, String disabled, String format) {}
    }

    @Comment({
            "E = Empty (Gets filled with the chunks)",
            "0 = Filler",
            "B = Buy Chunks",
            "P = Switch Profile",
            "M = Manage Profiles",
            "S = Settings"
    })
    private List<String> layout = List.of(
            "EEEEEEEEE",
            "EEEEEEEEE",
            "EEEEEEEEE",
            "EEEEEEEEE",
            "EEEEEEEEE",
            "0B0P0M0S0"
    );

    public static void reload() {
        instance = YamlConfigurations.load(
                new File(Fadlc.i().getDataFolder(), "menus/profiles.yml").toPath(),
                ProfilesConfig.class,
                PROPERTIES
        );
        Logger.info("profiles.yml automatically reloaded from disk.");
    }

    public static ProfilesConfig i() {
        if (instance == null) {
            instance = YamlConfigurations.update(
                    new File(Fadlc.i().getDataFolder(), "menus/profiles.yml").toPath(),
                    ProfilesConfig.class,
                    PROPERTIES
            );
            AutoReload.watch(Fadlc.i().getDataFolder().toPath(), "menus/claim.yml", ProfilesConfig::reload);
        }
        return instance;
    }
}