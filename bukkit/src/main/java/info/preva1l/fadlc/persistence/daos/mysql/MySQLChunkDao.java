package info.preva1l.fadlc.persistence.daos.mysql;

import com.zaxxer.hikari.HikariDataSource;
import info.preva1l.fadlc.Fadlc;
import info.preva1l.fadlc.models.ChunkLoc;
import info.preva1l.fadlc.models.ClaimChunk;
import info.preva1l.fadlc.models.IClaimChunk;
import info.preva1l.fadlc.persistence.Dao;
import info.preva1l.fadlc.utils.Logger;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
public class MySQLChunkDao implements Dao<IClaimChunk> {
    private final HikariDataSource dataSource;

    /**
     * Get an object from the database by its id.
     *
     * @param id the id of the object to get.
     * @return an optional containing the object if it exists, or an empty optional if it does not.
     */
    @Override
    public Optional<IClaimChunk> get(UUID id) {
        throw new IllegalStateException();
    }

    @Override
    public Optional<IClaimChunk> get(String find) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("""
                        SELECT `location`, `timeClaimed`, `profile`
                        FROM `chunks`
                        WHERE location=?;""")) {
                statement.setString(1, find);
                final ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    final ChunkLoc loc = Fadlc.i().getGson().fromJson(resultSet.getString("location"), ChunkLoc.class);
                    final long timeClaimed = resultSet.getLong("timeClaimed");
                    final int profile = resultSet.getInt("profile");
                    return Optional.of(new ClaimChunk(loc, timeClaimed, profile));
                }
            }
        } catch (SQLException e) {
            Logger.severe("Failed to get chunk!", e);
        }
        return Optional.empty();
    }

    /**
     * Get all objects of type T from the database.
     *
     * @return a list of all objects of type T in the database.
     */
    @Override
    public List<IClaimChunk> getAll() {
        List<IClaimChunk> chunks = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("""
                        SELECT `location`, `server`, `timeClaimed`, `profile`
                        FROM `chunks`;""")) {
                final ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    final ChunkLoc loc = Fadlc.i().getGson().fromJson(resultSet.getString("location"), ChunkLoc.class);
                    final long timeClaimed = resultSet.getLong("timeClaimed");
                    final int profile = resultSet.getInt("profile");
                    chunks.add(new ClaimChunk(loc, timeClaimed, profile));
                }
            }
        } catch (SQLException e) {
            Logger.severe("Failed to get all chunks!", e);
        }
        return chunks;
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
                        INSERT INTO `chunks` (`location`, `timeClaimed`, `profile`)
                        VALUES (?, ?, ?)
                        ON DUPLICATE KEY UPDATE
                            `profile` = VALUES(`profile`);""")) {
                statement.setString(1, Fadlc.i().getGson().toJson(chunk.getLoc()));
                statement.setLong(2, chunk.getClaimedSince());
                statement.setInt(3, chunk.getProfileId());
                statement.execute();
            } catch (Exception e) {
                Logger.severe("Failed to save!", e);
            }
        } catch (SQLException e) {
            Logger.severe("Failed to add item to chunks!", e);
        }
    }

    /**
     * Update an object of type T in the database.
     *
     * @param iClaimChunk the object to update.
     * @param params      the parameters to update the object with.
     */
    @Override
    public void update(IClaimChunk iClaimChunk, String[] params) {

    }

    /**
     * Delete an object of type T from the database.
     *
     * @param iClaimChunk the object to delete.
     */
    @Override
    public void delete(IClaimChunk iClaimChunk) {

    }
}
