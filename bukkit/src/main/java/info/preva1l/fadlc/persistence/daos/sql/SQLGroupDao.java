package info.preva1l.fadlc.persistence.daos.sql;

import com.google.gson.reflect.TypeToken;
import com.zaxxer.hikari.HikariDataSource;
import info.preva1l.fadlc.models.claim.IProfileGroup;
import info.preva1l.fadlc.models.claim.ProfileGroup;
import info.preva1l.fadlc.models.claim.settings.GroupSetting;
import info.preva1l.fadlc.models.user.OfflineUser;
import info.preva1l.fadlc.models.user.User;
import info.preva1l.fadlc.persistence.daos.Dao;
import info.preva1l.fadlc.persistence.handlers.DataHandler;
import info.preva1l.fadlc.registry.GroupSettingsRegistry;
import info.preva1l.fadlc.utils.Logger;
import lombok.AllArgsConstructor;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created on 20/03/2025
 *
 * @author Preva1l
 */
@AllArgsConstructor
public abstract class SQLGroupDao implements Dao<IProfileGroup> {
    protected final HikariDataSource dataSource;
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
                    final List<User> users = DataHandler.GSON.fromJson(resultSet.getString("users"), usersType);
                    final Map<GroupSetting, Boolean> flags = settingsDeserialize(DataHandler.GSON.fromJson(resultSet.getString("settings"), settingsType));
                    return Optional.of(new ProfileGroup(uuid, id, name, users, flags));
                }
            }
        } catch (SQLException e) {
            Logger.severe("Failed to get group!", e);
        }
        return Optional.empty();
    }

    @Override
    public List<IProfileGroup> getAll() {
        throw new UnsupportedOperationException();
    }

    /**
     * Update an object of type T in the database.
     *
     * @param iProfileGroup the object to update.
     * @param params        the parameters to update the object with.
     */
    @Override
    public void update(IProfileGroup iProfileGroup, String[] params) {
        throw new UnsupportedOperationException();
    }

    /**
     * Delete an object of type T from the database.
     *
     * @param iProfileGroup the object to delete.
     */
    @Override
    public void delete(IProfileGroup iProfileGroup) {
        throw new UnsupportedOperationException();
    }

    protected PreparedStatement saveStatement(IProfileGroup group, PreparedStatement statement) throws SQLException {
        String users = DataHandler.GSON.toJson(group.getUsers());
        String flags = DataHandler.GSON.toJson(settingsSerialize(group.getSettings()), settingsType);
        statement.setString(1, group.getUniqueId().toString());
        statement.setInt(2, group.getId());
        statement.setString(3, group.getName());
        statement.setString(4, users);
        statement.setString(5, flags);
        return statement;
    }

    protected Map<GroupSetting, Boolean> settingsDeserialize(Map<String, Boolean> map) {
        Map<GroupSetting, Boolean> result = new HashMap<>();
        for (String setting : map.keySet()) {
            result.put(GroupSettingsRegistry.get(setting), map.get(setting));
        }
        return result;
    }

    protected Map<String, Boolean> settingsSerialize(Map<GroupSetting, Boolean> map) {
        Map<String, Boolean> result = new HashMap<>();
        for (GroupSetting setting : map.keySet()) {
            result.put(setting.getId(), map.get(setting));
        }
        return result;
    }
}
