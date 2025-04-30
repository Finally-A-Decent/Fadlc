package info.preva1l.fadlc.persistence.daos.sqlite;

import com.zaxxer.hikari.HikariDataSource;
import info.preva1l.fadlc.persistence.daos.sql.SQLUserDao;
import info.preva1l.fadlc.user.OnlineUser;
import info.preva1l.fadlc.utils.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class SQLiteUserDao extends SQLUserDao {
    public SQLiteUserDao(HikariDataSource dataSource) {
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
                    VALUES (?,?,?,?,?)
                    ON CONFLICT(`uniqueId`) DO UPDATE SET
                            `username` = excluded.`username`,
                            `availableChunks` = excluded.`availableChunks`,
                            `settings` = excluded.`settings`,
                            `usingProfile` = excluded.`usingProfile`;""")) {
                saveStatement(onlineUser, statement).execute();
            } catch (Exception e) {
                Logger.severe("Failed to save!", e);
            }
        } catch (Exception e) {
            Logger.severe("Failed to add item to users!", e);
        }
    }
}
