package info.preva1l.fadlc.persistence.handlers;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import info.preva1l.fadlc.Fadlc;
import info.preva1l.fadlc.config.Config;
import info.preva1l.fadlc.models.IClaimChunk;
import info.preva1l.fadlc.models.claim.IClaim;
import info.preva1l.fadlc.models.claim.IClaimProfile;
import info.preva1l.fadlc.models.claim.IProfileGroup;
import info.preva1l.fadlc.models.user.OnlineUser;
import info.preva1l.fadlc.persistence.DatabaseObject;
import info.preva1l.fadlc.persistence.DatabaseType;
import info.preva1l.fadlc.persistence.daos.Dao;
import info.preva1l.fadlc.persistence.daos.sqlite.*;
import info.preva1l.fadlc.utils.Logger;
import lombok.Getter;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SQLiteHandler implements DataHandler {
    private final Map<Class<?>, Dao<?>> daos = new HashMap<>();

    @Getter private boolean connected = false;

    private static final String DATABASE_FILE_NAME = "FadlcData.db";
    private File databaseFile;
    private HikariDataSource dataSource;

    @Override
    @Blocking
    public void connect() {
        try {
            databaseFile = new File(Fadlc.i().getDataFolder(), DATABASE_FILE_NAME);
            if (databaseFile.createNewFile()) {
                Logger.info("Created the SQLite database file");
            }

            Class.forName(DatabaseType.SQLITE.getDriverClass());

            HikariConfig config = new HikariConfig();
            config.setPoolName("FadlcHikariPool");
            config.setDriverClassName(DatabaseType.SQLITE.getDriverClass());
            config.setJdbcUrl("jdbc:sqlite:" + databaseFile.getAbsolutePath());
            config.setConnectionTestQuery("SELECT 1");
            config.setMaxLifetime(60000);
            config.setMaximumPoolSize(50);
            dataSource = new HikariDataSource(config);
            this.backupFlatFile(databaseFile);

            final String[] databaseSchema = getSchemaStatements(String.format("database/%s_schema.sql", Config.i().getStorage().getType().getId()));
            try (Statement statement = dataSource.getConnection().createStatement()) {
                for (String tableCreationStatement : databaseSchema) {
                    statement.execute(tableCreationStatement);
                }
            } catch (SQLException e) {
                destroy();
                throw new IllegalStateException("Failed to create database tables.", e);
            }
        } catch (IOException e) {
            Logger.severe("An exception occurred creating the database file", e);
            destroy();
        } catch (ClassNotFoundException e) {
            Logger.severe("Failed to load the necessary SQLite driver", e);
            destroy();
        }
        registerDaos();
        connected = true;
    }

    @SuppressWarnings("SameParameterValue")
    @NotNull
    private String[] getSchemaStatements(@NotNull String schemaFileName) throws IOException {
        return new String(Objects.requireNonNull(Fadlc.i().getResource(schemaFileName))
                .readAllBytes(), StandardCharsets.UTF_8).split(";");
    }

    private void backupFlatFile(@NotNull File file) {
        if (!file.exists()) {
            return;
        }

        final File backup = new File(file.getParent(), String.format("%s.bak", file.getName()));
        try {
            if (!backup.exists() || backup.delete()) {
                Files.copy(file.toPath(), backup.toPath());
            }
        } catch (IOException e) {
            Logger.warn("Failed to backup flat file database", e);
        }
    }

    @Override
    public void destroy() {
        if (dataSource != null) dataSource.close();
    }

    public void registerDaos() {
        registerDao(IClaim.class, new SQLiteClaimDao(dataSource));
        registerDao(IClaimProfile.class, new SQLiteProfileDao(dataSource));
        registerDao(OnlineUser.class, new SQLiteUserDao(dataSource));
        registerDao(IClaimChunk.class, new SQLiteChunkDao(dataSource));
        registerDao(IProfileGroup.class, new SQLiteGroupDao(dataSource));
    }

    @Override
    public void registerDao(Class<?> aClass, Dao<? extends DatabaseObject> dao) {
        daos.put(aClass, dao);
    }


    @Override
    public void wipeDatabase() {
        // nothing yet
    }

    @Override
    public <T extends DatabaseObject> Dao<T> getDao(Class<?> clazz) {
        if (!daos.containsKey(clazz))
            throw new IllegalArgumentException("No DAO registered for class " + clazz.getName());
        return (Dao<T>) daos.get(clazz);
    }
}
