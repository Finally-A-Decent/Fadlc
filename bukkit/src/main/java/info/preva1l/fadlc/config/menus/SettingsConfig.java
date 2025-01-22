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
public class SettingsConfig implements MenuConfig {
    private static SettingsConfig instance;
    private static final YamlConfigurationProperties PROPERTIES = YamlConfigurationProperties.newBuilder()
            .charset(StandardCharsets.UTF_8)
            .setNameFormatter(NameFormatters.LOWER_KEBAB_CASE).build();

    private String title = "&8Settings";

    private Lang lang = new Lang();

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Lang {
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
        private SettingToggle settingToggle = new SettingToggle(
                "click", "%setting%",
                List.of(
                        "%description%",
                        "",
                        "&7\u2023 &3Status: &f%status%",
                        "",
                        "&7\u2192 Click to toggle"
                ), "&aEnbaled", "&cDisabled"
        );
        private SettingButton settingCycle = new SettingButton(
                "click", "%setting%",
                List.of(
                        "%description%",
                        "",
                        "&8-------------------------",
                        "&f%previous%",
                        "&8> &3%current%",
                        "&f%next%",
                        "&8-------------------------",
                        "",
                        "&7\u2192 Left Click to cycle up",
                        "&7\u2192 Right Click to cycle down"
                )
        );
        private SettingButton settingInput = new SettingButton(
                "click", "%setting%",
                List.of(
                        "%description%",
                        "",
                        "&7\u2023 &3Current: &f%current%",
                        "",
                        "&7\u2192 Click to edit"
                )
        );

        private Settings settings = new Settings();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class Settings {
            private Setting messageLocation = new Setting(
                    Material.OAK_SIGN,
                    "Message Location",
                    List.of(
                            "Where all the claim messages get shown."
                    )
            );
            private Setting viewBorders = new Setting(
                    Material.OAK_SIGN,
                    "View Claim Borders",
                    List.of(
                            "Show claim border particles."
                    )
            );
            private Setting claimEnterNotification = new Setting(
                    Material.OAK_SIGN,
                    "Claim Enter Notification",
                    List.of(
                            "Get notified when entering a claim."
                    )
            );
        }

        public record Setting(Material icon, String name, List<String> description) {
        }
        public record SettingButton(String sound, String name, List<String> description) {
            public SoundType getSound() {
                return Sounds.getSound(sound);
            }
        }
        public record SettingToggle(String sound, String name, List<String> description, String enabled, String disabled) {
            public SoundType getSound() {
                return Sounds.getSound(sound);
            }
        }
    }

    @Comment({
            "0 = Filler",
            "B = Back",
            "X = Settings (Paginated)",
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
                new File(Fadlc.i().getDataFolder(), "menus/settings.yml").toPath(),
                SettingsConfig.class,
                PROPERTIES
        );
        Logger.info("settings.yml automatically reloaded from disk.");
    }

    public static SettingsConfig i() {
        if (instance == null) {
            instance = YamlConfigurations.update(
                    new File(Fadlc.i().getDataFolder(), "menus/settings.yml").toPath(),
                    SettingsConfig.class,
                    PROPERTIES
            );
            AutoReload.watch(Fadlc.i().getDataFolder().toPath(), "menus/settings.yml", SettingsConfig::reload);
        }
        return instance;
    }
}