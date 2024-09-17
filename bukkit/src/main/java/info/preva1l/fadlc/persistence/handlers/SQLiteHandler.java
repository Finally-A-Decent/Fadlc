package info.preva1l.fadlc.persistence.handlers;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import info.preva1l.fadlc.Fadlc;
import info.preva1l.fadlc.config.Config;
import info.preva1l.fadlc.models.claim.IClaim;
import info.preva1l.fadlc.models.claim.IClaimProfile;
import info.preva1l.fadlc.persistence.Dao;
import info.preva1l.fadlc.persistence.DatabaseHandler;
import info.preva1l.fadlc.persistence.DatabaseObject;
import info.preva1l.fadlc.persistence.daos.ClaimDao;
import info.preva1l.fadlc.persistence.daos.ProfileDao;
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
import java.util.*;

public class SQLiteHandler implements DatabaseHandler {
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

            Class.forName("org.sqlite.JDBC");

            HikariConfig config = new HikariConfig();
            config.setPoolName("FadlcHikariPool");
            config.setDriverClassName("org.sqlite.JDBC");
            config.setJdbcUrl("jdbc:sqlite:" + databaseFile.getAbsolutePath());
            config.setConnectionTestQuery("SELECT 1");
            config.setMaxLifetime(60000);
            config.setIdleTimeout(45000);
            config.setMaximumPoolSize(50);
            dataSource = new HikariDataSource(config);
            this.backupFlatFile(databaseFile);

            final String[] databaseSchema = getSchemaStatements(String.format("database/%s_schema.sql", Config.getInstance().getStorage().getType().getId()));
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
        registerDao(IClaim.class, new ClaimDao(dataSource));
        registerDao(IClaimProfile.class, new ProfileDao(dataSource));
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
    public <T extends DatabaseObject> List<T> getAll(Class<T> clazz) {
        return (List<T>) getDao(clazz).getAll();
    }

    @Override
    public <T extends DatabaseObject> Optional<T> get(Class<T> clazz, UUID id) {
        return (Optional<T>) getDao(clazz).get(id);
    }

    @Override
    public <T extends DatabaseObject> Optional<T> search(Class<T> clazz, String name) {
        return (Optional<T>) getDao(clazz).get(name);
    }

    @Override
    public <T extends DatabaseObject> void save(Class<T> clazz, T t) {
        getDao(clazz).save(t);
    }

    @Override
    public <T extends DatabaseObject> void update(Class<T> clazz, T t, String[] params) {
        getDao(clazz).update(t, params);
    }

    @Override
    public <T extends DatabaseObject> void delete(Class<T> clazz, T t) {
        getDao(clazz).delete(t);
    }

    @Override
    public <T extends DatabaseObject> void deleteSpecific(Class<T> clazz, T t, Object o) {
        getDao(clazz).deleteSpecific(t, o);
    }

    /**
     * Gets the DAO for a specific class.
     *
     * @param clazz The class to get the DAO for.
     * @param <T>   The type of the class.
     * @return The DAO for the specified class.
     */
    private <T extends DatabaseObject> Dao<T> getDao(Class<?> clazz) {
        if (!daos.containsKey(clazz))
            throw new IllegalArgumentException("No DAO registered for class " + clazz.getName());
        return (Dao<T>) daos.get(clazz);
    }
}
