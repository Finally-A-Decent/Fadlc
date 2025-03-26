package info.preva1l.fadlc.persistence.daos.sqlite;

import com.zaxxer.hikari.HikariDataSource;
import info.preva1l.fadlc.models.claim.IProfileGroup;
import info.preva1l.fadlc.persistence.daos.sql.SQLGroupDao;
import info.preva1l.fadlc.utils.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQLiteGroupDao extends SQLGroupDao {
    public SQLiteGroupDao(HikariDataSource dataSource) {
        super(dataSource);
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
                saveStatement(group, statement).execute();
            } catch (Exception e) {
                Logger.severe("Failed to save!", e);
            }
        } catch (SQLException e) {
            Logger.severe("Failed to add item to groups!", e);
        }
    }
}
