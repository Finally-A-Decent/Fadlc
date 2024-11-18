package info.preva1l.fadlc.config;

import de.exlll.configlib.*;
import info.preva1l.fadlc.Fadlc;
import info.preva1l.fadlc.utils.Logger;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.File;
import java.nio.charset.StandardCharsets;

@Getter
@Configuration
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("FieldMayBeFinal")
public class ServerSettings {
    private static ServerSettings instance;

    private static final String CONFIG_HEADER = """
            ##########################################
            #                  Fadlc                 #
            #              Server Settings           #
            ##########################################
            """;

    private static final YamlConfigurationProperties PROPERTIES = YamlConfigurationProperties.newBuilder()
            .charset(StandardCharsets.UTF_8)
            .setNameFormatter(NameFormatters.LOWER_KEBAB_CASE)
            .header(CONFIG_HEADER).build();

    @Comment("Must match your bungeecord/velocity configuration.")
    private String name = "my-server";

    public static void reload() {
        instance = YamlConfigurations.load(new File(Fadlc.i().getDataFolder(), "server.yml").toPath(), ServerSettings.class, PROPERTIES);
        Logger.info("server.yml automatically reloaded from disk.");
    }

    public static ServerSettings getInstance() {
        if (instance == null) {
            instance = YamlConfigurations.update(new File(Fadlc.i().getDataFolder(), "server.yml").toPath(), ServerSettings.class, PROPERTIES);
            AutoReload.watch(Fadlc.i().getDataFolder().toPath(), "server.yml", ServerSettings::reload);
        }

        return instance;
    }
}
