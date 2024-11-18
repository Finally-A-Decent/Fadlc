package info.preva1l.fadlc.persistence.daos.sqlite;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zaxxer.hikari.HikariDataSource;
import info.preva1l.fadlc.Fadlc;
import info.preva1l.fadlc.models.claim.IProfileGroup;
import info.preva1l.fadlc.models.claim.ProfileGroup;
import info.preva1l.fadlc.models.claim.settings.GroupSetting;
import info.preva1l.fadlc.models.user.OfflineUser;
import info.preva1l.fadlc.models.user.User;
import info.preva1l.fadlc.persistence.Dao;
import info.preva1l.fadlc.registry.GroupSettingsRegistry;
import info.preva1l.fadlc.utils.Logger;
import lombok.AllArgsConstructor;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@AllArgsConstructor
public class SQLiteGroupDao implements Dao<IProfileGroup> {
    private final HikariDataSource dataSource;
    private static final Type settingsType = new TypeToken<Map<String, Boolean>>(){}.getType();
    private static final Type usersType = new TypeToken<List<OfflineUser>>(){}.getType();

    /**
     * Get an object from the database by its id.
     *
     * @param uniqueId the id of the object to get.
     * @return an optional containing the object if it exists, or an empty optional if it does not.
     */
    @Override
    public Optional<IProfileGroup> get(UUID uniqueId) {
        Gson gson = Fadlc.i().getGson();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("""
                        SELECT `id`, `name`, `users`, `settings`
                        FROM `groups`
                        WHERE `uuid`=?;""")) {
                statement.setString(1, uniqueId.toString());
                final ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    final UUID uuid = uniqueId;
                    final int id = resultSet.getInt("id");
                    final String name = resultSet.getString("name");
                    final List<User> users = gson.fromJson(resultSet.getString("users"), usersType);
                    final Map<GroupSetting, Boolean> flags = settingsDeserialize(gson.fromJson(resultSet.getString("settings"), settingsType));
                    return Optional.of(new ProfileGroup(uuid, id, name, users, flags));
                }
            }
        } catch (SQLException e) {
            Logger.severe("Failed to get group!", e);
        }
        return Optional.empty();
    }

    /**
     * Get all objects of type T from the database.
     *
     * @return a list of all objects of type T in the database.
     */
    @Override
    public List<IProfileGroup> getAll() {
        return List.of();
    }

    /**
     * Save an object of type T to the database.
     *
     * @param group the object to save.
     */
    @Override
    public void save(IProfileGroup group) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("""
                        INSERT INTO `groups`
                        (`uuid`, `id`, `name`, `users`, `settings`)
                        VALUES (?,?,?,?,?)
                        ON CONFLICT(`uuid`) DO UPDATE SET
                            `name` = excluded.`name`,
                            `users` = excluded.`users`,
                            `settings` = excluded.`settings`;""")) {
                String users = Fadlc.i().getGson().toJson(group.getUsers());
                String flags = Fadlc.i().getGson().toJson(settingsSerialize(group.getSettings()), settingsType);
                statement.setString(1, group.getUniqueId().toString());
                statement.setInt(2, group.getId());
                statement.setString(3, group.getName());
                statement.setString(4, users);
                statement.setString(5, flags);
                statement.execute();
            } catch (Exception e) {
                Logger.severe("Failed to save!", e);
            }
        } catch (SQLException e) {
            Logger.severe("Failed to add item to groups!", e);
        }
    }

    /**
     * Update an object of type T in the database.
     *
     * @param iProfileGroup the object to update.
     * @param params        the parameters to update the object with.
     */
    @Override
    public void update(IProfileGroup iProfileGroup, String[] params) {

    }

    /**
     * Delete an object of type T from the database.
     *
     * @param iProfileGroup the object to delete.
     */
    @Override
    public void delete(IProfileGroup iProfileGroup) {

    }

    private Map<GroupSetting, Boolean> settingsDeserialize(Map<String, Boolean> map) {
        Map<GroupSetting, Boolean> result = new HashMap<>();
        for (String setting : map.keySet()) {
            result.put(GroupSettingsRegistry.get(setting), map.get(setting));
        }
        return result;
    }

    private Map<String, Boolean> settingsSerialize(Map<GroupSetting, Boolean> map) {
        Map<String, Boolean> result = new HashMap<>();
        for (GroupSetting setting : map.keySet()) {
            result.put(setting.getId(), map.get(setting));
        }
        return result;
    }
}
