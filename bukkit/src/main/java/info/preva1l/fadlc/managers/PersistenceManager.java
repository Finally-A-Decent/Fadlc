package info.preva1l.fadlc.managers;

import info.preva1l.fadlc.config.Config;
import info.preva1l.fadlc.persistence.DatabaseObject;
import info.preva1l.fadlc.persistence.DatabaseType;
import info.preva1l.fadlc.persistence.handlers.DataHandler;
import info.preva1l.fadlc.persistence.handlers.MySQLHandler;
import info.preva1l.fadlc.persistence.handlers.SQLiteHandler;
import info.preva1l.fadlc.utils.Executors;
import info.preva1l.fadlc.utils.Logger;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public final class PersistenceManager {
    @Getter private static final PersistenceManager instance = new PersistenceManager();

    private final ExecutorService threadPool;
    private final Map<DatabaseType, Class<? extends DataHandler>> databaseHandlers = new HashMap<>();
    private final DataHandler handler;

    private PersistenceManager() {
        Logger.info("Connecting to Database and populating caches...");
        threadPool = Executors.V_THREAD_P_TASK;
        databaseHandlers.put(DatabaseType.MARIADB, MySQLHandler.class);
        databaseHandlers.put(DatabaseType.MYSQL, MySQLHandler.class);
        databaseHandlers.put(DatabaseType.SQLITE, SQLiteHandler.class);

        this.handler = initHandler();
        Logger.info("Connected to Database and populated caches!");
    }

    public void connect() {
        instance.handler.connect();
    }

    public <T extends DatabaseObject> CompletableFuture<List<T>> getAll(Class<T> clazz) {
        if (!isConnected()) {
            Logger.severe("Tried to perform database action when the database is not connected!");
            return CompletableFuture.completedFuture(List.of());
        }
        return CompletableFuture.supplyAsync(() -> handler.getAll(clazz), threadPool);
    }

    public <T extends DatabaseObject> CompletableFuture<Optional<T>> get(Class<T> clazz, UUID id) {
        if (!isConnected()) {
            Logger.severe("Tried to perform database action when the database is not connected!");
            return CompletableFuture.completedFuture(Optional.empty());
        }
        return CompletableFuture.supplyAsync(() -> handler.get(clazz, id), threadPool);
    }

    public <T extends DatabaseObject> CompletableFuture<Void> save(Class<T> clazz, T t) {
        if (!isConnected()) {
            Logger.severe("Tried to perform database action when the database is not connected!");
            return CompletableFuture.completedFuture(null);
        }
        return CompletableFuture.supplyAsync(() -> {
            handler.save(clazz, t);
            return null;
        }, threadPool);
    }

    public <T extends DatabaseObject> CompletableFuture<Void> delete(Class<T> clazz, T t) {
        if (!isConnected()) {
            Logger.severe("Tried to perform database action when the database is not connected!");
            return CompletableFuture.completedFuture(null);
        }
        return CompletableFuture.supplyAsync(() -> {
            handler.delete(clazz, t);
            return null;
        }, threadPool);
    }

    public <T extends DatabaseObject> CompletableFuture<Void> update(Class<T> clazz, T t, String[] params) {
        if (!isConnected()) {
            Logger.severe("Tried to perform database action when the database is not connected!");
            return CompletableFuture.completedFuture(null);
        }
        return CompletableFuture.supplyAsync(() -> {
            handler.update(clazz, t, params);
            return null;
        }, threadPool);
    }

    public boolean isConnected() {
        return handler.isConnected();
    }

    public void shutdown() {
        handler.destroy();
    }

    private DataHandler initHandler() {
        DatabaseType type = Config.i().getStorage().getType();
        Logger.info("DB Type: %s".formatted(type.getFriendlyName()));
        try {
            Class<? extends DataHandler> handlerClass = databaseHandlers.get(type);
            if (handlerClass == null) {
                throw new IllegalStateException("No handler for database type %s registered!".formatted(type.getFriendlyName()));
            }
            return handlerClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
