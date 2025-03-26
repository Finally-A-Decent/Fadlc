package info.preva1l.fadlc.persistence.daos.mysql;

import com.zaxxer.hikari.HikariDataSource;
import info.preva1l.fadlc.models.IClaimChunk;
import info.preva1l.fadlc.persistence.daos.sql.SQLChunkDao;
import info.preva1l.fadlc.utils.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySQLChunkDao extends SQLChunkDao {
    public MySQLChunkDao(HikariDataSource dataSource) {
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
                        ON DUPLICATE KEY UPDATE
                            `profile` = VALUES(`profile`);""")) {
                saveStatement(chunk, statement).execute();
            } catch (Exception e) {
                Logger.severe("Failed to save!", e);
            }
        } catch (SQLException e) {
            Logger.severe("Failed to add item to chunks!", e);
        }
    }
}
