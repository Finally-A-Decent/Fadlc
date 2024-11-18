package info.preva1l.fadlc.config;

import de.exlll.configlib.*;
import info.preva1l.fadlc.Fadlc;
import info.preva1l.fadlc.config.misc.PerformanceMode;
import info.preva1l.fadlc.models.claim.settings.GroupSetting;
import info.preva1l.fadlc.persistence.DatabaseType;
import info.preva1l.fadlc.registry.GroupSettingsRegistry;
import info.preva1l.fadlc.utils.Logger;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Getter
@Configuration
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("FieldMayBeFinal")
public class Config {
    private static Config instance;

    private static final String CONFIG_HEADER = """
            ##########################################
            #                  Fadlc                 #
            #      Finally a Decent Land Claim       #
            ##########################################
            """;

    private static final YamlConfigurationProperties PROPERTIES = YamlConfigurationProperties.newBuilder()
            .charset(StandardCharsets.UTF_8)
            .setNameFormatter(NameFormatters.LOWER_KEBAB_CASE)
            .header(CONFIG_HEADER).build();

    private int maxProfiles = 10;

    private Formatting formatting = new Formatting();

    @Comment({
            "What should Fadlc optimize for?",
            "TICK_TIME = More caching to prevent cpu heavy tasks",
            "MEMORY = Less caching to prevent excess memory usage"
    })
    private PerformanceMode performanceMode = PerformanceMode.TICK_TIME;

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Formatting {
        private String numbers = "#,###.00";

        private Time time = new Time();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class Time {
            private String seconds = "%ds";
            private String minutes = "%dm, %ds";
            private String hours = "%dh, %dm, %ds";
            private String days = "%dd, %dh, %dm, %ds";
            private String months = "%dm, %dd, %dh, %dm, %ds";
            private String years = "%dy, %dm, %dd, %dh, %dm, %ds";
        }
    }

    private Groups groupDefaults = new Groups();

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Groups {
        private First first = new First();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class First extends GroupConfig {
            private String name = "Default";
            private Map<String, Boolean> settings = Map.of(
                    GroupSettingsRegistry.BREAK_BLOCKS.get().getId(), false,
                    GroupSettingsRegistry.PLACE_BLOCKS.get().getId(), false,
                    GroupSettingsRegistry.ENTER.get().getId(), true
            );
        }

        private Second second = new Second();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class Second extends GroupConfig {
            private String name = "Group 2";
            private Map<String, Boolean> settings = Map.of(
                    GroupSettingsRegistry.BREAK_BLOCKS.get().getId(), false,
                    GroupSettingsRegistry.PLACE_BLOCKS.get().getId(), false,
                    GroupSettingsRegistry.ENTER.get().getId(), true
            );
        }

        private Third third = new Third();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class Third extends GroupConfig {
            private String name = "Group 3";
            private Map<String, Boolean> settings = Map.of(
                    GroupSettingsRegistry.BREAK_BLOCKS.get().getId(), true,
                    GroupSettingsRegistry.PLACE_BLOCKS.get().getId(), true,
                    GroupSettingsRegistry.ENTER.get().getId(), true
            );
        }

        private Fourth fourth = new Fourth();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class Fourth extends GroupConfig {
            private String name = "Group 4";
            private Map<String, Boolean> settings = Map.of(
                    GroupSettingsRegistry.BREAK_BLOCKS.get().getId(), true,
                    GroupSettingsRegistry.PLACE_BLOCKS.get().getId(), true,
                    GroupSettingsRegistry.ENTER.get().getId(), true
            );
        }

        private Fifth fifth = new Fifth();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class Fifth extends GroupConfig {
            private String name = "Group 5";
            private Map<String, Boolean> settings = Map.of(
                    GroupSettingsRegistry.BREAK_BLOCKS.get().getId(), true,
                    GroupSettingsRegistry.PLACE_BLOCKS.get().getId(), true,
                    GroupSettingsRegistry.ENTER.get().getId(), true
            );
        }

        private String owner = "Owner";
    }

    private Jobs jobs = new Jobs();

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Jobs {
        @Comment("How big the thread pool is for jobs. Increase if jobs are taking a long time to complete.")
        private int poolSize = 3;

        @Comment("How often claim data should save, in minutes.")
        private int claimSaveInterval = 30;

        @Comment("How often user data should save, in minutes.")
        private int usersSaveInterval = 10;

        @Comment("Enable to stop the [JOBS] logs.")
        private boolean shutTheHellUp = false;
    }

    private Storage storage = new Storage();

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Storage {
        @Comment("Allowed: SQLITE, MYSQL, MARIADB, MONGO")
        private DatabaseType type = DatabaseType.SQLITE;

        private String host = "localhost";
        private int port = 3306;
        private String database = "fadlc";
        private String username = "root";
        private String password = "myFancyPassword";
        private boolean useSsl = false;
    }

    public static void reload() {
        instance = YamlConfigurations.load(new File(Fadlc.i().getDataFolder(), "config.yml").toPath(), Config.class, PROPERTIES);
        Logger.info("Configuration automatically reloaded from disk.");
    }

    public static Config i() {
        if (instance == null) {
            instance = YamlConfigurations.update(new File(Fadlc.i().getDataFolder(), "config.yml").toPath(), Config.class, PROPERTIES);
            AutoReload.watch(Fadlc.i().getDataFolder().toPath(), "config.yml", Config::reload);
        }

        return instance;
    }

    public abstract static class GroupConfig {
        public abstract Map<String, Boolean> getSettings();

        public Map<GroupSetting, Boolean> getRealSettings() {
            Map<GroupSetting, Boolean> map = new HashMap<>();
            for (String key : getSettings().keySet()) {
                map.put(GroupSettingsRegistry.get(key), getSettings().get(key));
            }
            return map;
        }
    }
}