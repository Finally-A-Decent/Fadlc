package info.preva1l.fadlc.persistence;

import info.preva1l.fadlc.Fadlc;
import info.preva1l.fadlc.config.Config;
import info.preva1l.fadlc.persistence.handlers.DataHandler;
import info.preva1l.fadlc.persistence.handlers.MySQLHandler;
import info.preva1l.fadlc.persistence.handlers.SQLiteHandler;
import info.preva1l.fadlc.utils.Executors;
import info.preva1l.trashcan.flavor.annotations.Close;
import info.preva1l.trashcan.flavor.annotations.Configure;
import info.preva1l.trashcan.flavor.annotations.Service;
import info.preva1l.trashcan.flavor.annotations.inject.Inject;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

@Service(priority = 10)
public final class DataService {
    @Getter public static final DataService instance = new DataService();

    @Inject private Fadlc plugin;
    @Inject public Logger logger;

    private final ExecutorService threadPool;
    private final Map<DatabaseType, Class<? extends DataHandler>> databaseHandlers = new HashMap<>();
    private DataHandler handler;

    private DataService() {
        threadPool = Executors.V_THREAD_P_TASK;
        databaseHandlers.put(DatabaseType.MARIADB, MySQLHandler.class);
        databaseHandlers.put(DatabaseType.MYSQL, MySQLHandler.class);
        databaseHandlers.put(DatabaseType.SQLITE, SQLiteHandler.class);
    }

    @Configure
    public void configure() {
        this.handler = initHandler();
        instance.handler.connect();
        Skins.load();
    }

    public <T extends DatabaseObject> CompletableFuture<List<T>> getAll(Class<T> clazz) {
        if (!isConnected()) {
            logger.severe("Tried to perform database action when the database is not connected!");
            return CompletableFuture.completedFuture(List.of());
        }
        return CompletableFuture.supplyAsync(() -> handler.getAll(clazz), threadPool);
    }

    public <T extends DatabaseObject> CompletableFuture<Optional<T>> get(Class<T> clazz, UUID id) {
        if (!isConnected()) {
            logger.severe("Tried to perform database action when the database is not connected!");
            return CompletableFuture.completedFuture(Optional.empty());
        }
        return CompletableFuture.supplyAsync(() -> handler.get(clazz, id), threadPool);
    }

    public <T extends DatabaseObject> CompletableFuture<Void> save(Class<T> clazz, T t) {
        if (!isConnected()) {
            logger.severe("Tried to perform database action when the database is not connected!");
            return CompletableFuture.completedFuture(null);
        }
        return CompletableFuture.supplyAsync(() -> {
            handler.save(clazz, t);
            return null;
        }, threadPool);
    }

    public <T extends DatabaseObject> CompletableFuture<Void> delete(Class<T> clazz, T t) {
        if (!isConnected()) {
            logger.severe("Tried to perform database action when the database is not connected!");
            return CompletableFuture.completedFuture(null);
        }
        return CompletableFuture.supplyAsync(() -> {
            handler.delete(clazz, t);
            return null;
        }, threadPool);
    }

    public <T extends DatabaseObject> CompletableFuture<Void> update(Class<T> clazz, T t, String[] params) {
        if (!isConnected()) {
            logger.severe("Tried to perform database action when the database is not connected!");
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

    @Close
    public void shutdown() {
        handler.destroy();
        Skins.save();
    }

    private DataHandler initHandler() {
        DatabaseType type = Config.i().getStorage().getType();
        logger.info("DB Type: %s".formatted(type.getFriendlyName()));
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
