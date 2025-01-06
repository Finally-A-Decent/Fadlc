package info.preva1l.fadlc.persistence.daos.mysql;

import com.google.gson.reflect.TypeToken;
import com.zaxxer.hikari.HikariDataSource;
import info.preva1l.fadlc.Fadlc;
import info.preva1l.fadlc.models.user.BukkitUser;
import info.preva1l.fadlc.models.user.OnlineUser;
import info.preva1l.fadlc.models.user.settings.SettingHolder;
import info.preva1l.fadlc.persistence.Dao;
import info.preva1l.fadlc.utils.Logger;
import lombok.AllArgsConstructor;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
public class MySQLUserDao implements Dao<OnlineUser> {
    private final HikariDataSource dataSource;
    private static final Type SETTINGS_TYPE = new TypeToken<List<SettingHolder<?, ?>>>(){}.getType();

    /**
     * Get an object from the database by its id.
     *
     * @param id the id of the object to get.
     * @return an optional containing the object if it exists, or an empty optional if it does not.
     */
    @Override
    public Optional<OnlineUser> get(UUID id) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("""
                    SELECT  `uniqueId`, `username`, `availableChunks`, `settings`, `usingProfile`
                    FROM `users`
                    WHERE `uniqueId`=?;""")) {
                statement.setString(1, id.toString());
                final ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    final UUID ownerUUID = id;
                    final String ownerName = resultSet.getString("username");
                    final int availableChunks = resultSet.getInt("availableChunks");
                    final List<SettingHolder<?, ?>> settings =
                            Fadlc.i().getGson().fromJson(resultSet.getString("settings"), SETTINGS_TYPE);
                    final int usingProfile = resultSet.getInt("usingProfile");
                    return Optional.of(new BukkitUser(ownerName, ownerUUID,
                            availableChunks, usingProfile, settings));
                }
            } catch (Exception e) {
                Logger.severe("Failed to get!", e);
            }
        } catch (SQLException e) {
            Logger.severe("Failed to get user!", e);
        }
        return Optional.empty();
    }

    /**
     * Get all objects of type T from the database.
     *
     * @return a list of all objects of type T in the database.
     */
    @Override
    public List<OnlineUser> getAll() {
        return List.of();
    }

    /**
     * Save an object of type T to the database.
     *
     * @param onlineUser the object to save.
     */
    @Override
    public void save(OnlineUser onlineUser) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO `users`
                        (`uniqueId`, `username`, `availableChunks`, `settings`, `usingProfile`)
                    VALUES (?, ?, ?, ?, ?)
                    ON DUPLICATE KEY UPDATE
                        `username` = VALUES(`username`),
                        `availableChunks` = VALUES(`availableChunks`),
                        `settings` = VALUES(`settings`),
                        `usingProfile` = VALUES(`usingProfile`);""")) {
                statement.setString(1, onlineUser.getUniqueId().toString());
                statement.setString(2, onlineUser.getName());
                statement.setInt(3, onlineUser.getAvailableChunks());
                statement.setString(4, Fadlc.i().getGson().toJson(onlineUser.getSettings(), SETTINGS_TYPE));
                statement.setInt(5, onlineUser.getClaimWithProfile().getId());
                statement.execute();
            } catch (Exception e) {
                Logger.severe("Failed to save!", e);
            }
        } catch (SQLException e) {
            Logger.severe("Failed to add item to users!", e);
        }
    }

    /**
     * Update an object of type T in the database.
     *
     * @param onlineUser the object to update.
     * @param params     the parameters to update the object with.
     */
    @Override
    public void update(OnlineUser onlineUser, String[] params) {

    }

    /**
     * Delete an object of type T from the database.
     *
     * @param onlineUser the object to delete.
     */
    @Override
    public void delete(OnlineUser onlineUser) {

    }
}
