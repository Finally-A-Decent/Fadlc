package info.preva1l.fadlc.persistence.daos.mysql;

import com.zaxxer.hikari.HikariDataSource;
import info.preva1l.fadlc.claim.IClaim;
import info.preva1l.fadlc.persistence.daos.sql.SQLClaimDao;
import info.preva1l.fadlc.utils.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySQLClaimDao extends SQLClaimDao {
    public MySQLClaimDao(HikariDataSource dataSource) {
        super(dataSource);
    }

    /**
     * Save an object of type T to the database.
     *
     * @param claim the object to save.
     */
    @Override
    public void save(IClaim claim) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("""
                     INSERT INTO `claims`
                         (`ownerUUID`, `ownerUsername`, `profiles`, `chunks`)
                     VALUES (?, ?, ?, ?)
                     ON DUPLICATE KEY UPDATE
                         `ownerUsername` = VALUES(`ownerUsername`),
                         `profiles` = VALUES(`profiles`),
                         `chunks` = VALUES(`chunks`);""")) {
                saveStatement(claim, statement).execute();
            } catch (Exception e) {
                Logger.severe("Failed to save!", e);
            }
        } catch (SQLException e) {
            Logger.severe("Failed to add item to claims!", e);
        }
    }
}
