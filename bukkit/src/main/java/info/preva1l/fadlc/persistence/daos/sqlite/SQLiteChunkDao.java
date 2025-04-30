package info.preva1l.fadlc.persistence.daos.sqlite;

import com.zaxxer.hikari.HikariDataSource;
import info.preva1l.fadlc.claim.IClaimChunk;
import info.preva1l.fadlc.persistence.daos.sql.SQLChunkDao;
import info.preva1l.fadlc.utils.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQLiteChunkDao extends SQLChunkDao {
    public SQLiteChunkDao(HikariDataSource dataSource) {
        super(dataSource);
    }

    /**
     * Save an object of type T to the database.
     *
     * @param chunk the object to save.
     */
    @Override
    public void save(IClaimChunk chunk) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("""
                        INSERT INTO `chunks`
                            (`uuid`, `location`, `timeClaimed`, `profile`)
                        VALUES (?, ?, ?, ?)
                        ON CONFLICT(`uuid`) DO UPDATE SET
                            `profile` = excluded.`profile`;""")) {
                saveStatement(chunk, statement).execute();
            } catch (Exception e) {
                Logger.severe("Failed to save!", e);
            }
        } catch (SQLException e) {
            Logger.severe("Failed to add item to chunks!", e);
        }
    }
}
