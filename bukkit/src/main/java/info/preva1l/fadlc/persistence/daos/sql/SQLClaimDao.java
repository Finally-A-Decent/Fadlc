package info.preva1l.fadlc.persistence.daos.sql;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zaxxer.hikari.HikariDataSource;
import info.preva1l.fadlc.claim.Claim;
import info.preva1l.fadlc.claim.IClaim;
import info.preva1l.fadlc.claim.IClaimChunk;
import info.preva1l.fadlc.claim.IClaimProfile;
import info.preva1l.fadlc.claim.services.ClaimService;
import info.preva1l.fadlc.models.ChunkLoc;
import info.preva1l.fadlc.persistence.DataService;
import info.preva1l.fadlc.persistence.daos.Dao;
import info.preva1l.fadlc.persistence.handlers.DataHandler;
import info.preva1l.fadlc.user.OfflineUser;
import info.preva1l.fadlc.utils.Logger;
import lombok.AllArgsConstructor;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created on 20/03/2025
 *
 * @author Preva1l
 */
@AllArgsConstructor
public abstract class SQLClaimDao implements Dao<IClaim> {
    protected final HikariDataSource dataSource;
    protected static final Type stringListType = new TypeToken<List<String>>(){}.getType();

    /**
     * Get an object from the database by its id.
     *
     * @param id the id of the object to get.
     * @return an optional containing the object if it exists, or an empty optional if it does not.
     */
    @Override
    public Optional<IClaim> get(UUID id) {
        Gson gson = DataHandler.GSON;
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("""
                        SELECT `ownerUUID`, `ownerUsername`, `profiles`, `chunks`
                        FROM `claims`
                        WHERE `ownerUUID`=?;""")) {
                statement.setString(1, id.toString());
                final ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    final UUID ownerUUID = id;
                    final String ownerName = resultSet.getString("ownerUsername");
                    final Map<ChunkLoc, Integer> chunks =
                            chunkDeserialize(gson.fromJson(resultSet.getString("chunks"), stringListType));
                    final Map<Integer, IClaimProfile> profiles =
                            profileDeserialize(gson.fromJson(resultSet.getString("profiles"), stringListType));
                    return Optional.of(new Claim(new OfflineUser(ownerUUID, ownerName), chunks, profiles));
                }
            }
        } catch (SQLException e) {
            Logger.severe("Failed to get claim!", e);
        }
        return Optional.empty();
    }

    /**
     * Get all objects of type T from the database.
     *
     * @return a list of all objects of type T in the database.
     */
    @Override
    public List<IClaim> getAll() {
        List<IClaim> claims = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("""
                        SELECT  `ownerUUID`, `ownerUsername`, `profiles`, `chunks`
                        FROM `claims`;""")) {
                final ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    final UUID ownerUUID = UUID.fromString(resultSet.getString("ownerUUID"));
                    final String ownerName = resultSet.getString("ownerUsername");
                    final Map<ChunkLoc, Integer> chunks =
                            chunkDeserialize(DataHandler.GSON.fromJson(resultSet.getString("chunks"), stringListType));
                    final Map<Integer, IClaimProfile> profiles =
                            profileDeserialize(DataHandler.GSON.fromJson(resultSet.getString("profiles"), stringListType));
                    claims.add(new Claim(new OfflineUser(ownerUUID, ownerName), chunks, profiles));
                }
            }
        } catch (SQLException e) {
            Logger.severe("Failed to get all claims!", e);
        }
        return claims;
    }

    /**
     * Update an object of type T in the database.
     *
     * @param iClaim the object to update.
     * @param params      the parameters to update the object with.
     */
    @Override
    public void update(IClaim iClaim, String[] params) {
        throw new UnsupportedOperationException();
    }

    /**
     * Delete an object of type T from the database.
     *
     * @param iClaim the object to delete.
     */
    @Override
    public void delete(IClaim iClaim) {
        throw new UnsupportedOperationException();
    }

    protected PreparedStatement saveStatement(IClaim claim, PreparedStatement statement) throws SQLException {
        String profiles = DataHandler.GSON.toJson(profileSerialize(claim.getProfiles()));
        String chunks = DataHandler.GSON.toJson(chunkSerialize(claim.getClaimedChunks()));
        statement.setString(1, claim.getOwner().getUniqueId().toString());
        statement.setString(2, claim.getOwner().getName());
        statement.setString(3, profiles);
        statement.setString(4, chunks);
        return statement;
    }

    protected List<String> profileSerialize(Map<Integer, IClaimProfile> profiles) {
        List<String> list = new ArrayList<>();
        for (IClaimProfile profile : profiles.values()) {
            list.add(profile.getUniqueId().toString());
        }
        return list;
    }

    protected Map<Integer, IClaimProfile> profileDeserialize(List<String> profiles) {
        Map<Integer, IClaimProfile> ret = new HashMap<>();
        for (String profileId : profiles) {
            IClaimProfile profile = DataService.getInstance()
                    .get(IClaimProfile.class, UUID.fromString(profileId)).join().orElseThrow();
            ret.put(profile.getId(), profile);
        }
        return ret;
    }

    protected List<String> chunkSerialize(Map<ChunkLoc, Integer> profiles) {
        List<String> list = new ArrayList<>();
        for (ChunkLoc chunk : profiles.keySet()) {
            list.add(DataHandler.GSON.toJson(chunk));
        }
        return list;
    }

    protected Map<ChunkLoc, Integer> chunkDeserialize(List<String> profiles) {
        Map<ChunkLoc, Integer> ret = new HashMap<>();
        for (String locStr : profiles) {
            ChunkLoc loc = DataHandler.GSON.fromJson(locStr, ChunkLoc.class);
            IClaimChunk chunk = ClaimService.getInstance().getChunk(loc);
            ret.put(chunk.getLoc(), chunk.getProfileId());
        }
        return ret;
    }
}
