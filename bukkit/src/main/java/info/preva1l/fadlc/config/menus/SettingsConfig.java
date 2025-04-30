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
public class SettingsConfig implements MenuConfig<PaginatedLang> {
    private static SettingsConfig instance;
    private static final YamlConfigurationProperties PROPERTIES = YamlConfigurationProperties.newBuilder()
            .charset(StandardCharsets.UTF_8)
            .setNameFormatter(NameFormatters.LOWER_KEBAB_CASE).build();

    private String title = "&8Settings";

    private Lang lang = new Lang();

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Lang extends PaginatedLang {
        private ConfigurableItem back = new ConfigurableItem(
                Material.FEATHER, 0, "click",
                "&3Back", List.of("&7\u2192 Click to go back")
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
        private SettingCycle settingCycle = new SettingCycle(
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
        private SettingInput settingInput = new SettingInput(
                "click", "%setting%",
                List.of(
                        "%description%",
                        "",
                        "&7\u2023 &3Current: &f%current%",
                        "",
                        "&7\u2192 Click to edit"
                ),
                "&8Editing setting: %setting%",
                "&cYour input was not valid for this setting!"
        );

        private Settings settings = new Settings();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class Settings {
            private Setting messageLocation = new Setting(
                    Material.OAK_SIGN,
                    "&fMessage Location",
                    List.of(
                            "&7Where all the claim messages get shown."
                    )
            );
            private Setting viewBorders = new Setting(
                    Material.STRUCTURE_VOID,
                    "&fView Claim Borders",
                    List.of(
                            "&7Show claim border particles."
                    )
            );
            private Setting claimLeaveEnterNotification = new Setting(
                    Material.NAME_TAG,
                    "&fClaim Leave/Enter Notification",
                    List.of(
                            "&7Get notified when entering or leaving a claim."
                    )
            );
        }

        public record Setting(Material icon, String name, List<String> description) {}

        public record SettingInput(String sound, String name, List<String> description, String prompt, String invalid) {
            public SoundType getSound() {
                return Sounds.getSound(sound);
            }
        }

        public record SettingCycle(String sound, String name, List<String> description) {
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