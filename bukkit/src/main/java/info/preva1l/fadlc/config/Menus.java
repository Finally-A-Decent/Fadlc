package info.preva1l.fadlc.config;

import de.exlll.configlib.Configuration;
import de.exlll.configlib.NameFormatters;
import de.exlll.configlib.YamlConfigurationProperties;
import de.exlll.configlib.YamlConfigurations;
import info.preva1l.fadlc.Fadlc;
import info.preva1l.fadlc.config.misc.ConfigurableItem;
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
public class Menus {
    private static Menus instance;

    private static final String CONFIG_HEADER = """
            ##########################################
            #                  Fadlc                 #
            #    Miscellaneous Menu Configuration    #
            ##########################################
            """;

    private static final YamlConfigurationProperties PROPERTIES = YamlConfigurationProperties.newBuilder()
            .charset(StandardCharsets.UTF_8)
            .setNameFormatter(NameFormatters.LOWER_KEBAB_CASE)
            .header(CONFIG_HEADER).build();

    private Filler filler = new Filler();

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Filler implements ConfigurableItem {
        private Material material = Material.BLACK_STAINED_GLASS_PANE;
        private int modelData = 0;
        private String name = "&r ";
        private List<String> lore = List.of("&8I <3 Fadlc");
    }

    private NoItems noItems = new NoItems();

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class NoItems implements ConfigurableItem {
        private Material material = Material.BARRIER;
        private int modelData = 0;
        private String name = "&cNo Items Found!";
        private List<String> lore = List.of("");
    }

    public static void reload() {
        instance = YamlConfigurations.load(new File(Fadlc.i().getDataFolder(), "menus/misc.yml").toPath(), Menus.class, PROPERTIES);
        Logger.info("Configuration '%s' automatically reloaded from disk.".formatted("menus/misc.yml"));
    }

    public static Menus getInstance() {
        if (instance == null) {
            instance = YamlConfigurations.update(new File(Fadlc.i().getDataFolder(), "menus/misc.yml").toPath(), Menus.class, PROPERTIES);
            AutoReload.watch(Fadlc.i().getDataFolder().toPath(), "menus/misc.yml", Menus::reload);
        }

        return instance;
    }
}