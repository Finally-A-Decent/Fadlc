package info.preva1l.fadlc.persistence.daos.mysql;

import com.zaxxer.hikari.HikariDataSource;
import info.preva1l.fadlc.models.claim.IClaimProfile;
import info.preva1l.fadlc.persistence.daos.sql.SQLProfileDao;
import info.preva1l.fadlc.utils.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySQLProfileDao extends SQLProfileDao {
    public MySQLProfileDao(HikariDataSource dataSource) {
        super(dataSource);
    }

    /**
     * Save an object of type T to the database.
     *
     * @param profile the object to save.
     */
    @Override
    public void save(IClaimProfile profile) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO `profiles`
                            (`uuid`, `id`, `name`, `groups`, `flags`, `border`, `parent`)
                        VALUES (?, ?, ?, ?, ?, ?, ?)
                        ON DUPLICATE KEY UPDATE
                            `id` = VALUES(`id`),
                            `name` = VALUES(`name`),
                            `groups` = VALUES(`groups`),
                            `flags` = VALUES(`flags`),
                            `border` = VALUES(`border`);""")) {
                saveStatement(profile, statement).execute();
            } catch (Exception e) {
                Logger.severe("Failed to save!", e);
            }
        } catch (SQLException e) {
            Logger.severe("Failed to add item to profiles!", e);
        }
    }
}
