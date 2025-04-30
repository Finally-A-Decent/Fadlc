package info.preva1l.fadlc.persistence.daos.sql;

import com.zaxxer.hikari.HikariDataSource;
import info.preva1l.fadlc.claim.ClaimChunk;
import info.preva1l.fadlc.claim.IClaimChunk;
import info.preva1l.fadlc.models.ChunkLoc;
import info.preva1l.fadlc.persistence.daos.Dao;
import info.preva1l.fadlc.persistence.handlers.DataHandler;
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

/**
 * Created on 20/03/2025
 *
 * @author Preva1l
 */
@AllArgsConstructor
public abstract class SQLChunkDao implements Dao<IClaimChunk> {
    protected final HikariDataSource dataSource;

    @Override
    public Optional<IClaimChunk> get(UUID id) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("""
                        SELECT `uuid`, `location`, `timeClaimed`, `profile`
                        FROM `chunks`
                        WHERE uuid=?;""")) {
                statement.setString(1, id.toString());
                final ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    final ChunkLoc loc = DataHandler.GSON.fromJson(resultSet.getString("location"), ChunkLoc.class);
                    final long timeClaimed = resultSet.getLong("timeClaimed");
                    final int profile = resultSet.getInt("profile");
                    return Optional.of(new ClaimChunk(id, loc, timeClaimed, profile));
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
                        SELECT `uuid`, `location`, `timeClaimed`, `profile`
                        FROM `chunks`;""")) {
                final ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    final UUID id = UUID.fromString(resultSet.getString("uniqueId"));
                    final ChunkLoc loc = DataHandler.GSON.fromJson(resultSet.getString("location"), ChunkLoc.class);
                    final long timeClaimed = resultSet.getLong("timeClaimed");
                    final int profile = resultSet.getInt("profile");
                    chunks.add(new ClaimChunk(id, loc, timeClaimed, profile));
                }
            }
        } catch (SQLException e) {
            Logger.severe("Failed to get all chunks!", e);
        }
        return chunks;
    }

    /**
     * Update an object of type T in the database.
     *
     * @param iClaimChunk the object to update.
     * @param params      the parameters to update the object with.
     */
    @Override
    public void update(IClaimChunk iClaimChunk, String[] params) {
        throw new UnsupportedOperationException();
    }

    /**
     * Delete an object of type T from the database.
     *
     * @param iClaimChunk the object to delete.
     */
    @Override
    public void delete(IClaimChunk iClaimChunk) {
        throw new UnsupportedOperationException();
    }

    public PreparedStatement saveStatement(IClaimChunk chunk, PreparedStatement statement) throws SQLException {
        statement.setString(1, chunk.getUniqueId().toString());
        statement.setString(2, DataHandler.GSON.toJson(chunk.getLoc()));
        statement.setLong(3, chunk.getClaimedSince());
        statement.setInt(4, chunk.getProfileId());
        return statement;
    }
}
