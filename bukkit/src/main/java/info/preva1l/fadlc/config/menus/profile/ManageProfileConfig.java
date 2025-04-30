package info.preva1l.fadlc.config.menus.profile;

import de.exlll.configlib.*;
import info.preva1l.fadlc.Fadlc;
import info.preva1l.fadlc.config.AutoReload;
import info.preva1l.fadlc.config.menus.MenuConfig;
import info.preva1l.fadlc.config.menus.lang.MenuLang;
import info.preva1l.fadlc.config.misc.ConfigurableItem;
import info.preva1l.fadlc.config.misc.FlagInfo;
import info.preva1l.fadlc.config.sounds.SoundType;
import info.preva1l.fadlc.config.sounds.Sounds;
import info.preva1l.fadlc.menus.lib.ItemBuilder;
import info.preva1l.fadlc.utils.Logger;
import info.preva1l.fadlc.utils.Text;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Created on 2/04/2025
 *
 * @author Preva1l
 */
@Getter
@Configuration
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("FieldMayBeFinal")
public class ManageProfileConfig implements MenuConfig<MenuLang> {
    private static ManageProfileConfig instance;

    private static final String FILE_NAME = "menus/manage-profile.yml";
    private static final YamlConfigurationProperties PROPERTIES = YamlConfigurationProperties.newBuilder()
            .charset(StandardCharsets.UTF_8)
            .setNameFormatter(NameFormatters.LOWER_KEBAB_CASE).build();

    private String title = "&8%profile% - Manage";

    private Lang lang = new Lang();

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Lang extends MenuLang {
        private ConfigurableItem back = new ConfigurableItem(
                Material.FEATHER, 0, "click",
                "&3Back", List.of("&7\u2192 Click to go back")
        );

        private Map<Integer, Material> groupIcons = Map.of(
                1, Material.WOODEN_SWORD,
                2, Material.STONE_SWORD,
                3, Material.IRON_SWORD,
                4, Material.GOLDEN_SWORD,
                5, Material.DIAMOND_SWORD
        );

        private Group group = new Group(
                "click", "%group%",
                List.of(
                        "&3Info:",
                        "&7\u2023 &3Members: &f%members%",
                        "&3Settings:",
                        "%settings%",
                        "",
                        "&7\u2192 Click to manage this group"
                ), new FlagInfo("&aEnabled", "&cDisabled", "&7\u2023 &3%setting%: &r%status%")
        );

        public record Group(String sound, String name, List<String> lore, FlagInfo setting) {
            public ItemStack itemStack(Material material) {
                return new ItemBuilder(material)
                        .name(Text.text(name))
                        .lore(Text.list(lore))
                        .build();
            }

            public SoundType getSound() {
                return Sounds.getSound(sound);
            }
        }
    }

    @Comment({
            "0 = Filler",
            "B = Back",
            "F = Edit Flags",
            "1-5 = Edit Group X"
    })
    private List<String> layout = List.of(
            "001234500",
            "000000000",
            "0F0000000",
            "B00000000"
    );

    public static void reload() {
        instance = YamlConfigurations.load(
                new File(Fadlc.i().getDataFolder(), FILE_NAME).toPath(),
                ManageProfileConfig.class,
                PROPERTIES
        );
        Logger.info("Configuration '%s' automatically reloaded from disk.".formatted(FILE_NAME));
    }

    public static ManageProfileConfig i() {
        if (instance == null) {
            instance = YamlConfigurations.update(
                    new File(Fadlc.i().getDataFolder(), FILE_NAME).toPath(),
                    ManageProfileConfig.class,
                    PROPERTIES
            );
            AutoReload.watch(Fadlc.i().getDataFolder().toPath(), FILE_NAME, ManageProfileConfig::reload);
        }
        return instance;
    }
}
