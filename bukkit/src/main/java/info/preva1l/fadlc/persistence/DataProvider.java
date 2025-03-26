package info.preva1l.fadlc.persistence;

import info.preva1l.fadlc.managers.ClaimManager;
import info.preva1l.fadlc.managers.PersistenceManager;
import info.preva1l.fadlc.models.IClaimChunk;
import info.preva1l.fadlc.models.IPosition;
import info.preva1l.fadlc.models.claim.IClaim;
import info.preva1l.fadlc.models.claim.IProfileGroup;
import info.preva1l.fadlc.models.claim.settings.GroupSetting;
import info.preva1l.fadlc.models.user.OnlineUser;
import info.preva1l.fadlc.utils.Logger;

import java.util.List;
import java.util.Optional;

/**
 * Created on 20/03/2025
 *
 * @author Preva1l
 */
public interface DataProvider {
    default void loadData() {
        Logger.info("Loading Data...");
        PersistenceManager.getInstance().connect();
        populateCaches();
        Skins.load();
        Logger.info("Data initialized!");
    }

    default void shutdownData() {
        PersistenceManager.getInstance().shutdown();
        Skins.save();
    }

    private void populateCaches() {
        Logger.info("Populating Caches...");
        List<IClaimChunk> chunks = PersistenceManager.getInstance().getAll(IClaimChunk.class).join();
        chunks.forEach(chunk -> ClaimManager.getInstance().cacheChunk(chunk));
        Logger.info("Chunk Cache Populated!");

        List<IClaim> claims = PersistenceManager.getInstance().getAll(IClaim.class).join();
        claims.forEach(claim -> ClaimManager.getInstance().updateClaim(claim));
        Logger.info("Claim Cache Populated!");
    }


    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    default boolean isActionAllowed(OnlineUser user, IPosition location, GroupSetting setting) {
        Optional<IClaim> claimAtLocation = ClaimManager.getInstance().getClaimAt(location);

        if (claimAtLocation.isEmpty()) return true;
        if (claimAtLocation.get().getOwner().equals(user)) return true;

        IProfileGroup group = claimAtLocation.get().getProfile(location.getChunk()).orElseThrow().getPlayerGroup(user);

        return group.getSettings().get(setting);
    }
}
