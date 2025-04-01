package info.preva1l.fadlc.config.menus;

import de.exlll.configlib.*;
import info.preva1l.fadlc.Fadlc;
import info.preva1l.fadlc.config.AutoReload;
import info.preva1l.fadlc.config.menus.lang.PaginatedLang;
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
public class ProfilesConfig implements MenuConfig<PaginatedLang> {
    private static ProfilesConfig instance;
    private static final YamlConfigurationProperties PROPERTIES = YamlConfigurationProperties.newBuilder()
            .charset(StandardCharsets.UTF_8)
            .setNameFormatter(NameFormatters.LOWER_KEBAB_CASE).build();

    private String title = "&8Manage Profiles";

    private Lang lang = new Lang();

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Lang implements PaginatedLang {
        private ConfigurableItem back = new ConfigurableItem(
                Material.FEATHER, 0, "click",
                "&3Back", List.of("&7\u2192 Click to go back")
        );
        private ConfigurableItem previous = new ConfigurableItem(
                Material.ARROW, 0, "click",
                "&3Previous Page", List.of("&7\u2192 Click to go to the previous page")
        );
        private ConfigurableItem next = new ConfigurableItem(
                Material.ARROW, 0, "click",
                "&3Next Page", List.of("&7\u2192 Click to go to the next page")
        );
        private Profile profile = new Profile(
                "click", "%profile%",
                List.of(
                        "&3Info:",
                        "&7\u2023 &3Members: &f%members%",
                        "&7\u2023 &3Border: &f%border%",
                        "&7\u2023 &3Chunks: &f%chunks%",
                        "&3Flags:",
                        "%flags%"
                ), new Flag("&aEnabled", "&cDisabled", "&7\u2023 &3%flag%: &r%status%")
        );
        public record Profile(String sound, String name, List<String> lore, Flag flag) {
            public SoundType getSound() {
                return Sounds.getSound(sound);
            }
        }
        public record Flag(String enabled, String disabled, String format) {}
    }

    @Comment({
            "0 = Filler",
            "B = Back",
            "X = Profiles (Paginated)",
            "N = Next Page",
            "P = Previous Page",
    })
    private List<String> layout = List.of(
            "000000000",
            "0XXXXXXX0",
            "0XXXXXXX0",
            "B00P0N000"
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
            AutoReload.watch(Fadlc.i().getDataFolder().toPath(), "menus/profiles.yml", ProfilesConfig::reload);
        }
        return instance;
    }
}