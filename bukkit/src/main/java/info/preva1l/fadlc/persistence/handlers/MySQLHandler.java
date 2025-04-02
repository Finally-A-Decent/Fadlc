package info.preva1l.fadlc.persistence.handlers;

import com.zaxxer.hikari.HikariDataSource;
import info.preva1l.fadlc.Fadlc;
import info.preva1l.fadlc.config.Config;
import info.preva1l.fadlc.models.IClaimChunk;
import info.preva1l.fadlc.models.claim.IClaim;
import info.preva1l.fadlc.models.claim.IClaimProfile;
import info.preva1l.fadlc.models.claim.IProfileGroup;
import info.preva1l.fadlc.models.user.OnlineUser;
import info.preva1l.fadlc.persistence.DatabaseObject;
import info.preva1l.fadlc.persistence.daos.Dao;
import info.preva1l.fadlc.persistence.daos.mysql.*;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

public class MySQLHandler implements DataHandler {
    private final Map<Class<?>, Dao<?>> daos = new HashMap<>();
    private final String driverClass;
    @Getter private boolean connected = false;
    private HikariDataSource dataSource;

    public MySQLHandler() {
        this.driverClass = Config.i().getStorage().getType().getDriverClass();
    }

    @SuppressWarnings("SameParameterValue")
    @NotNull
    private String[] getSchemaStatements(@NotNull String schemaFileName) throws IOException {
        return new String(Objects.requireNonNull(Fadlc.i().getResource(schemaFileName))
                .readAllBytes(), StandardCharsets.UTF_8).split(";");
    }

    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void connect() {
        Config.Storage sql = Config.i().getStorage();

        dataSource = new HikariDataSource();
        dataSource.setDriverClassName(driverClass);
        dataSource.setJdbcUrl(String.format("jdbc:%s://%s:%d/%s", Config.i().getStorage().getType().getId(), sql.getHost(), sql.getPort(), sql.getDatabase()));
        dataSource.setUsername(sql.getUsername());
        dataSource.setPassword(sql.getPassword());

        dataSource.setMaximumPoolSize(10);
        dataSource.setMinimumIdle(10);
        dataSource.setMaxLifetime(1800000);
        dataSource.setKeepaliveTime(0);
        dataSource.setConnectionTimeout(5000);
        dataSource.setPoolName("FadlcHikariPool");

        final Properties properties = new Properties();
        properties.putAll(
                Map.of("cachePrepStmts", "true",
                        "prepStmtCacheSize", "250",
                        "prepStmtCacheSqlLimit", "2048",
                        "useServerPrepStmts", "true",
                        "useLocalSessionState", "true",
                        "useLocalTransactionState", "true"
                ));
        properties.putAll(
                Map.of(
                        "rewriteBatchedStatements", "true",
                        "cacheResultSetMetadata", "true",
                        "cacheServerConfiguration", "true",
                        "elideSetAutoCommits", "true",
                        "maintainTimeStats", "false")
        );
        dataSource.setDataSourceProperties(properties);

        try (Connection connection = dataSource.getConnection()) {
            final String[] databaseSchema = getSchemaStatements(String.format("database/%s_schema.sql", Config.i().getStorage().getType().getId()));
            try (Statement statement = connection.createStatement()) {
                for (String tableCreationStatement : databaseSchema) {
                    statement.execute(tableCreationStatement);
                }
                connected = true;
            } catch (SQLException e) {
                destroy();
                throw new IllegalStateException("Failed to create database tables. Please ensure you are running MySQL v8.0+ " +
                        "and that your connecting user account has privileges to create tables.", e);
            }
        } catch (SQLException | IOException e) {
            destroy();
            throw new IllegalStateException("Failed to establish a connection to the MySQL database. " +
                    "Please check the supplied database credentials in the config file", e);
        }
        registerDaos();
    }

    public void destroy() {
        if (dataSource != null) dataSource.close();
    }

    @Override
    public void wipeDatabase() {
        // do nun
    }

    public void registerDaos() {
        registerDao(IClaim.class, new MySQLClaimDao(dataSource));
        registerDao(IClaimProfile.class, new MySQLProfileDao(dataSource));
        registerDao(OnlineUser.class, new MySQLUserDao(dataSource));
        registerDao(IClaimChunk.class, new MySQLChunkDao(dataSource));
        registerDao(IProfileGroup.class, new MySQLGroupDao(dataSource));
    }

    @Override
    public void registerDao(Class<?> aClass, Dao<? extends DatabaseObject> dao) {
        daos.put(aClass, dao);
    }

    @Override
    public <T extends DatabaseObject> Dao<T> getDao(Class<?> clazz) {
        if (!daos.containsKey(clazz))
            throw new IllegalArgumentException("No DAO registered for class " + clazz.getName());
        return (Dao<T>) daos.get(clazz);
    }
}