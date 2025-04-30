package info.preva1l.fadlc.config;

import de.exlll.configlib.*;
import info.preva1l.fadlc.Fadlc;
import info.preva1l.fadlc.claim.registry.GroupSettingsRegistry;
import info.preva1l.fadlc.claim.settings.GroupSetting;
import info.preva1l.fadlc.config.misc.PerformanceMode;
import info.preva1l.fadlc.persistence.DatabaseType;
import info.preva1l.fadlc.utils.Logger;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Configuration
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("FieldMayBeFinal")
public class Config {
    private static Config instance;

    private static final String FILE_NAME = "config.yml";
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

    @Comment({"Toggle with /fadlc toggle",
            "When the plugin is disabled, claim protection will still be in place,",
            "but modifying & creating claims is disabled."})
    private boolean enabled = true;
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        save();
    }

    private boolean updateChecker = true;


    private General general = new General();

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class General {
        @Comment("How many chunks a user should receive upon their first time joining.")
        private int startingChunks = 1;

        @Comment("How many profiles a user should be able to create.")
        private int maxProfiles = 10;

        private List<String> disabledWorlds = List.of(
                "world_the_end",
                "world_nether"
        );
    }

    private Optimization optimization = new Optimization();

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Optimization {
        @Comment({
                "What should Fadlc optimize for?",
                "TICK_TIME = More caching to prevent cpu heavy tasks",
                "MEMORY = Less caching to prevent excess memory usage"
        })
        private PerformanceMode performanceMode = PerformanceMode.TICK_TIME;

        private int particleFrequencyMillis = 200;
        private int particleViewDistance = 30;
    }

    private Formatting formatting = new Formatting();

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Formatting {
        private String numbers = "#,###.00";
        private String date = "dd/MM/yyyy HH:mm";

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

        public DecimalFormat numbers() {
            return new DecimalFormat(numbers);
        }
    }

    private Profiles profileDefaults = new Profiles();

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Profiles {
        private String name = "&7%username%'s Claim";
        private String border = "default";
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
                    GroupSettingsRegistry.USE_DOORS.get().getId(), false,
                    GroupSettingsRegistry.USE_BUTTONS.get().getId(), false,
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
                    GroupSettingsRegistry.USE_DOORS.get().getId(), true,
                    GroupSettingsRegistry.USE_BUTTONS.get().getId(), false,
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
                    GroupSettingsRegistry.USE_DOORS.get().getId(), true,
                    GroupSettingsRegistry.USE_BUTTONS.get().getId(), true,
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
                    GroupSettingsRegistry.USE_DOORS.get().getId(), true,
                    GroupSettingsRegistry.USE_BUTTONS.get().getId(), true,
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
                    GroupSettingsRegistry.USE_DOORS.get().getId(), true,
                    GroupSettingsRegistry.USE_BUTTONS.get().getId(), true,
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

    public void save() {
        YamlConfigurations.save(new File(Fadlc.i().getDataFolder(), FILE_NAME).toPath(), Config.class, this);
    }

    public static void reload() {
        instance = YamlConfigurations.load(new File(Fadlc.i().getDataFolder(), FILE_NAME).toPath(), Config.class, PROPERTIES);
        Logger.info("Configuration '%s' automatically reloaded from disk.".formatted(FILE_NAME));
    }

    public static Config i() {
        if (instance == null) {
            instance = YamlConfigurations.update(new File(Fadlc.i().getDataFolder(), FILE_NAME).toPath(), Config.class, PROPERTIES);
            AutoReload.watch(Fadlc.i().getDataFolder().toPath(), FILE_NAME, Config::reload);
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