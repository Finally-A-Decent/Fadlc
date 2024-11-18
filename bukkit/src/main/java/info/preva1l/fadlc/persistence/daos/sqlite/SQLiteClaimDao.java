package info.preva1l.fadlc.persistence.daos.sqlite;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zaxxer.hikari.HikariDataSource;
import info.preva1l.fadlc.Fadlc;
import info.preva1l.fadlc.managers.ClaimManager;
import info.preva1l.fadlc.managers.PersistenceManager;
import info.preva1l.fadlc.models.ChunkLoc;
import info.preva1l.fadlc.models.IClaimChunk;
import info.preva1l.fadlc.models.claim.Claim;
import info.preva1l.fadlc.models.claim.IClaim;
import info.preva1l.fadlc.models.claim.IClaimProfile;
import info.preva1l.fadlc.models.user.OfflineUser;
import info.preva1l.fadlc.persistence.Dao;
import info.preva1l.fadlc.utils.Logger;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.Blocking;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@AllArgsConstructor
public class SQLiteClaimDao implements Dao<IClaim> {
    private final HikariDataSource dataSource;
    private static final Type stringListType = new TypeToken<List<String>>(){}.getType();

    /**
     * Get an object from the database by its id.
     *
     * @param id the id of the object to get.
     * @return an optional containing the object if it exists, or an empty optional if it does not.
     */
    @Override
    public Optional<IClaim> get(UUID id) {
        Gson gson = Fadlc.i().getGson();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("""
                        SELECT  `ownerUUID`, `ownerUsername`, `profiles`, `chunks`
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
        Gson gson = Fadlc.i().getGson();
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
                            chunkDeserialize(gson.fromJson(resultSet.getString("chunks"), stringListType));
                    final Map<Integer, IClaimProfile> profiles =
                            profileDeserialize(gson.fromJson(resultSet.getString("profiles"), stringListType));
                    claims.add(new Claim(new OfflineUser(ownerUUID, ownerName), chunks, profiles));
                }
            }
        } catch (SQLException e) {
            Logger.severe("Failed to get all claims!", e);
        }
        return claims;
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
                        VALUES (?,?,?,?)
                        ON CONFLICT(`ownerUUID`) DO UPDATE SET
                            `ownerUsername` = excluded.`ownerUsername`,
                            `profiles` = excluded.`profiles`,
                            `chunks` = excluded.`chunks`;""")) {

                String profiles = Fadlc.i().getGson().toJson(profileSerialize(claim.getProfiles()));
                String chunks = Fadlc.i().getGson().toJson(chunkSerialize(claim.getClaimedChunks()));
                statement.setString(1, claim.getOwner().getUniqueId().toString());
                statement.setString(2, claim.getOwner().getName());
                statement.setString(3, profiles);
                statement.setString(4, chunks);
                statement.execute();
            } catch (Exception e) {
                Logger.severe("Failed to save!", e);
            }
        } catch (SQLException e) {
            Logger.severe("Failed to add item to claims!", e);
        }
    }

    /**
     * Update an object of type T in the database.
     *
     * @param claim the object to update.
     * @param params   the parameters to update the object with.
     */
    @Override
    public void update(IClaim claim, String[] params) {

    }

    /**
     * Delete an object of type T from the database.
     *
     * @param claim the object to delete.
     */
    @Override
    public void delete(IClaim claim) {

    }

    private List<String> profileSerialize(Map<Integer, IClaimProfile> profiles) {
        List<String> list = new ArrayList<>();
        for (IClaimProfile profile : profiles.values()) {
            list.add(profile.getUniqueId().toString());
        }
        return list;
    }

    @Blocking
    private Map<Integer, IClaimProfile> profileDeserialize(List<String> profiles) {
        Map<Integer, IClaimProfile> ret = new HashMap<>();
        for (String profileId : profiles) {
            IClaimProfile profile = PersistenceManager.getInstance()
                    .get(IClaimProfile.class, UUID.fromString(profileId)).join().orElseThrow();
            ret.put(profile.getId(), profile);
        }
        return ret;
    }

    private List<String> chunkSerialize(Map<ChunkLoc, Integer> profiles) {
        List<String> list = new ArrayList<>();
        for (ChunkLoc chunk : profiles.keySet()) {
            list.add(Fadlc.i().getGson().toJson(chunk));
        }
        return list;
    }

    private Map<ChunkLoc, Integer> chunkDeserialize(List<String> profiles) {
        Map<ChunkLoc, Integer> ret = new HashMap<>();
        for (String locStr : profiles) {
            ChunkLoc loc = Fadlc.i().getGson().fromJson(locStr, ChunkLoc.class);
            IClaimChunk chunk = ClaimManager.getInstance().getChunk(loc);
            ret.put(chunk.getLoc(), chunk.getProfileId());
        }
        return ret;
    }
}
