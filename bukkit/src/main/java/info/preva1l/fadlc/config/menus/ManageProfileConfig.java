package info.preva1l.fadlc.config.menus;

import de.exlll.configlib.*;
import info.preva1l.fadlc.Fadlc;
import info.preva1l.fadlc.config.AutoReload;
import info.preva1l.fadlc.config.menus.lang.PaginatedLang;
import info.preva1l.fadlc.config.misc.ConfigurableItem;
import info.preva1l.fadlc.config.misc.FlagInfo;
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

/**
 * Created on 2/04/2025
 *
 * @author Preva1l
 */
@Getter
@Configuration
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("FieldMayBeFinal")
public class ManageProfileConfig implements MenuConfig<PaginatedLang> {
    private static ManageProfileConfig instance;
    private static final YamlConfigurationProperties PROPERTIES = YamlConfigurationProperties.newBuilder()
            .charset(StandardCharsets.UTF_8)
            .setNameFormatter(NameFormatters.LOWER_KEBAB_CASE).build();

    private String title = "&8Manage Profiles";

    private Lang lang = new Lang();

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Lang implements PaginatedLang {
        private ConfigurableItem previous = new ConfigurableItem(
                Material.ARROW, 0, "click",
                "&3Previous Page", List.of("&7\u2192 Click to go to the previous page")
        );
        private ConfigurableItem next = new ConfigurableItem(
                Material.ARROW, 0, "click",
                "&3Next Page", List.of("&7\u2192 Click to go to the next page")
        );

        private Flag flag = new Flag(
                "click", "click", "%flag%",
                List.of(
                        "%info%",
                        "",
                        "&7\u2192 Click to go to toggle"
                ),
                new FlagInfo("&aEnabled", "&cDisabled", "&7\u2023 &3Status: &r%status%")
        );

        public record Flag(String enableSound, String disableSound, String name, List<String> lore, FlagInfo info) {
            public SoundType getEnableSound() {
                return Sounds.getSound(enableSound);
            }
            public SoundType getDisableSound() {
                return Sounds.getSound(disableSound);
            }
        }
    }

    @Comment({
            "0 = Filler",
            "B = Back",
            "X = Profile Flags (Paginated)",
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
                new File(Fadlc.i().getDataFolder(), "menus/profile-flags.yml").toPath(),
                ManageProfileConfig.class,
                PROPERTIES
        );
        Logger.info("profiles.yml automatically reloaded from disk.");
    }

    public static ManageProfileConfig i() {
        if (instance == null) {
            instance = YamlConfigurations.update(
                    new File(Fadlc.i().getDataFolder(), "menus/profiles.yml").toPath(),
                    ManageProfileConfig.class,
                    PROPERTIES
            );
            AutoReload.watch(Fadlc.i().getDataFolder().toPath(), "menus/profiles.yml", ManageProfileConfig::reload);
        }
        return instance;
    }
}
