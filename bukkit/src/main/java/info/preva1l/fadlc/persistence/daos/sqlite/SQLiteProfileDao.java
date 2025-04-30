package info.preva1l.fadlc.persistence.daos.sqlite;

import com.zaxxer.hikari.HikariDataSource;
import info.preva1l.fadlc.claim.IClaimProfile;
import info.preva1l.fadlc.persistence.daos.sql.SQLProfileDao;
import info.preva1l.fadlc.utils.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQLiteProfileDao extends SQLProfileDao {
    public SQLiteProfileDao(HikariDataSource dataSource) {
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
                    VALUES (?,?,?,?,?,?,?)
                    ON CONFLICT(`uuid`) DO UPDATE SET
                        `id` = excluded.`id`,
                        `name` = excluded.`name`,
                        `groups` = excluded.`groups`,
                        `flags` = excluded.`flags`,
                        `border` = excluded.`border`;""")) {
                saveStatement(profile, statement).execute();
            } catch (Exception e) {
                Logger.severe("Failed to save!", e);
            }
        } catch (SQLException e) {
            Logger.severe("Failed to add item to profiles!", e);
        }
    }
}
