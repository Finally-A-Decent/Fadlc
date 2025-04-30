package info.preva1l.fadlc.persistence.daos.sql;

import com.google.gson.reflect.TypeToken;
import com.zaxxer.hikari.HikariDataSource;
import info.preva1l.fadlc.claim.ClaimProfile;
import info.preva1l.fadlc.claim.IClaimProfile;
import info.preva1l.fadlc.claim.IProfileGroup;
import info.preva1l.fadlc.claim.registry.ProfileFlagsRegistry;
import info.preva1l.fadlc.claim.settings.ProfileFlag;
import info.preva1l.fadlc.persistence.DataService;
import info.preva1l.fadlc.persistence.daos.Dao;
import info.preva1l.fadlc.persistence.handlers.DataHandler;
import info.preva1l.fadlc.utils.Logger;
import lombok.AllArgsConstructor;
import org.bukkit.Material;

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
public abstract class SQLProfileDao implements Dao<IClaimProfile> {
    protected final HikariDataSource dataSource;
    private static final Type stringListType = new TypeToken<List<String>>(){}.getType();
    private static final Type flagsType = new TypeToken<Map<String, Boolean>>(){}.getType();

    /**
     * Get an object from the database by its id.
     *
     * @param uniqueId the id of the object to get.
     * @return an optional containing the object if it exists, or an empty optional if it does not.
     */
    @Override
    public Optional<IClaimProfile> get(UUID uniqueId) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("""
                        SELECT  `id`, `parent`, `name`, `groups`, `flags`, `border`
                        FROM `profiles`
                        WHERE `uuid`=?;""")) {
                statement.setString(1, uniqueId.toString());
                final ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    final UUID uuid = uniqueId;
                    final String name = resultSet.getString("name");
                    final UUID parent = UUID.fromString(resultSet.getString("parent"));
                    final int id = resultSet.getInt("id");
                    final Map<Integer, IProfileGroup> groups = groupDeserialize(DataHandler.GSON.fromJson(resultSet.getString("groups"), stringListType));
                    final Map<ProfileFlag, Boolean> flags = flagsDeserialize(DataHandler.GSON.fromJson(resultSet.getString("flags"), flagsType));
                    final String border = resultSet.getString("border");
                    return Optional.of(new ClaimProfile(parent, uuid, name, id, Material.BLUE_WOOL, groups, flags, border));
                }
            }
        } catch (SQLException e) {
            Logger.severe("Failed to get profile!", e);
        }
        return Optional.empty();
    }

    @Override
    public List<IClaimProfile> getAll() {
        throw new UnsupportedOperationException();
    }

    /**
     * Update an object of type T in the database.
     *
     * @param iProfileGroup the object to update.
     * @param params        the parameters to update the object with.
     */
    @Override
    public void update(IClaimProfile iProfileGroup, String[] params) {
        throw new UnsupportedOperationException();
    }

    /**
     * Delete an object of type T from the database.
     *
     * @param iProfileGroup the object to delete.
     */
    @Override
    public void delete(IClaimProfile iProfileGroup) {
        throw new UnsupportedOperationException();
    }

    protected PreparedStatement saveStatement(IClaimProfile profile, PreparedStatement statement) throws SQLException {
        String groups = DataHandler.GSON.toJson(groupSerialize(profile.getGroups().values()));
        String flags = DataHandler.GSON.toJson(flagsSerialize(profile.getFlags()));
        statement.setString(1, profile.getUniqueId().toString());
        statement.setInt(2, profile.getId());
        statement.setString(3, profile.getName());
        statement.setString(4, groups);
        statement.setString(5, flags);
        statement.setString(6, profile.getBorder());
        statement.setString(7, profile.getParent().getOwner().getUniqueId().toString());
        return statement;
    }

    protected List<String> groupSerialize(Collection<IProfileGroup> groups) {
        List<String> list = new ArrayList<>();
        for (IProfileGroup group : groups) {
            list.add(group.getUniqueId().toString());
        }
        return list;
    }

    protected Map<Integer, IProfileGroup> groupDeserialize(List<String> groups) {
        Map<Integer, IProfileGroup> list = new HashMap<>();
        for (String uuidStr : groups) {
            UUID uuid = UUID.fromString(uuidStr);
            DataService.getInstance().get(IProfileGroup.class, uuid).join().ifPresent(group -> {
                list.put(group.getId(), group);
            });
        }
        return list;
    }

    protected Map<ProfileFlag, Boolean> flagsDeserialize(Map<String, Boolean> map) {
        Map<ProfileFlag, Boolean> result = new HashMap<>();
        for (String flag : map.keySet()) {
            result.put(ProfileFlagsRegistry.get(flag), map.get(flag));
        }
        return result;
    }

    protected Map<String, Boolean> flagsSerialize(Map<ProfileFlag, Boolean> map) {
        Map<String, Boolean> result = new HashMap<>();
        for (ProfileFlag setting : map.keySet()) {
            result.put(setting.getId(), map.get(setting));
        }
        return result;
    }
}
