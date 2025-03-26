package info.preva1l.fadlc.persistence.daos.mysql;

import com.zaxxer.hikari.HikariDataSource;
import info.preva1l.fadlc.models.user.OnlineUser;
import info.preva1l.fadlc.persistence.daos.sql.SQLUserDao;
import info.preva1l.fadlc.utils.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySQLUserDao extends SQLUserDao {
    public MySQLUserDao(HikariDataSource dataSource) {
        super(dataSource);
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
                saveStatement(onlineUser, statement).execute();
            } catch (Exception e) {
                Logger.severe("Failed to save!", e);
            }
        } catch (SQLException e) {
            Logger.severe("Failed to add item to users!", e);
        }
    }
}
