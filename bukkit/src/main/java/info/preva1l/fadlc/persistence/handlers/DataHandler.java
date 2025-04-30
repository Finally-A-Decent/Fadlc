package info.preva1l.fadlc.persistence.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import info.preva1l.fadlc.persistence.DatabaseObject;
import info.preva1l.fadlc.persistence.daos.Dao;
import info.preva1l.fadlc.persistence.gson.SettingSerializer;
import info.preva1l.fadlc.user.settings.Setting;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * An interface for interacting with a data source via passing DAOs.
 */
public interface DataHandler {
    Gson GSON = new GsonBuilder().registerTypeHierarchyAdapter(Setting.class, new SettingSerializer()).create();

    boolean isConnected();
    void connect();
    void destroy();
    void wipeDatabase();

    /**
     * Get all the objects of type <T>
     *
     * @param clazz type to get
     * @return list of <T>
     */
    default <T extends DatabaseObject> List<T> getAll(Class<T> clazz) {
        return (List<T>) getDao(clazz).getAll();
    }

    /**
     * Get an object of a certain class and id.
     *
     * @param clazz type to get.
     * @param id the uuid of the object.
     * @return an optional of the object, an empty optional if the object was not found.
     */
    default <T extends DatabaseObject> Optional<T> get(Class<T> clazz, UUID id) {
        return (Optional<T>) getDao(clazz).get(id);
    }

    /**
     * Save an object of a certain type.
     *
     * @param clazz class of the object
     * @param t object
     */
    default <T extends DatabaseObject> void save(Class<T> clazz, T t) {
        getDao(clazz).save(t);
    }

    /**
     * Update an object in the database.
     *
     * @param clazz class of the object
     * @param t the new object
     * @param params update params
     */
    default <T extends DatabaseObject> void update(Class<T> clazz, T t, String[] params) {
        getDao(clazz).update(t, params);
    }

    /**
     * Delete an object of <T>.
     *
     * @param clazz class of object
     * @param t object
     */
    default  <T extends DatabaseObject> void delete(Class<T> clazz, T t) {
        getDao(clazz).delete(t);
    }

    /**
     * Gets the DAO for a specific class.
     *
     * @param clazz The class to get the DAO for.
     * @param <T>   The type of the class.
     * @return The DAO for the specified class.
     */
    <T extends DatabaseObject> Dao<T> getDao(Class<?> clazz);

    /**
     * Registers a dao with the database handler.
     *
     * @param clazz the daos linkage
     * @param dao the instance of the dao
     */
    void registerDao(Class<?> clazz, Dao<? extends DatabaseObject> dao);
}