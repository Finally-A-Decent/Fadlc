package info.preva1l.fadlc.persistence.daos.sql;

import com.google.gson.reflect.TypeToken;
import com.zaxxer.hikari.HikariDataSource;
import info.preva1l.fadlc.models.user.BukkitUser;
import info.preva1l.fadlc.models.user.OnlineUser;
import info.preva1l.fadlc.models.user.settings.Setting;
import info.preva1l.fadlc.persistence.daos.Dao;
import info.preva1l.fadlc.persistence.handlers.DataHandler;
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

/**
 * Created on 20/03/2025
 *
 * @author Preva1l
 */
@AllArgsConstructor
public abstract class SQLUserDao implements Dao<OnlineUser> {
    protected final HikariDataSource dataSource;
    private static final Type SETTINGS_TYPE = new TypeToken<List<Setting<?>>>(){}.getType();

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
                    SELECT `uniqueId`, `username`, `availableChunks`, `settings`, `usingProfile`
                    FROM `users`
                    WHERE `uniqueId`=?;""")) {
                statement.setString(1, id.toString());
                final ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    final UUID ownerUUID = id;
                    final String ownerName = resultSet.getString("username");
                    final int availableChunks = resultSet.getInt("availableChunks");
                    final List<Setting<?>> settings =
                            DataHandler.GSON.fromJson(resultSet.getString("settings"), SETTINGS_TYPE);
                    final int usingProfile = resultSet.getInt("usingProfile");
                    return Optional.of(new BukkitUser(ownerName, ownerUUID,
                            availableChunks, usingProfile, settings));
                }
            } catch (Exception e) {
                Logger.severe("Failed to get!", e);
            }
        } catch (Exception e) {
            Logger.severe("Failed to get user!", e);
        }
        return Optional.empty();
    }


    @Override
    public List<OnlineUser> getAll() {
        throw new UnsupportedOperationException();
    }

    /**
     * Update an object of type T in the database.
     *
     * @param iProfileGroup the object to update.
     * @param params        the parameters to update the object with.
     */
    @Override
    public void update(OnlineUser iProfileGroup, String[] params) {
        throw new UnsupportedOperationException();
    }

    /**
     * Delete an object of type T from the database.
     *
     * @param iProfileGroup the object to delete.
     */
    @Override
    public void delete(OnlineUser iProfileGroup) {
        throw new UnsupportedOperationException();
    }

    protected PreparedStatement saveStatement(OnlineUser onlineUser, PreparedStatement statement) throws SQLException {
        statement.setString(1, onlineUser.getUniqueId().toString());
        statement.setString(2, onlineUser.getName());
        statement.setInt(3, onlineUser.getAvailableChunks());
        statement.setString(4, DataHandler.GSON.toJson(onlineUser.getSettings(), SETTINGS_TYPE));
        statement.setInt(5, onlineUser.getClaimWithProfile().getId());
        return statement;
    }
}
