package info.preva1l.fadlc.api;

import info.preva1l.fadlc.managers.ClaimManager;
import info.preva1l.fadlc.managers.IClaimManager;
import info.preva1l.fadlc.managers.IUserManager;
import info.preva1l.fadlc.managers.UserManager;
import info.preva1l.fadlc.models.IPosition;
import info.preva1l.fadlc.models.claim.IClaim;
import info.preva1l.fadlc.models.claim.IProfileGroup;
import info.preva1l.fadlc.models.claim.settings.GroupSetting;
import info.preva1l.fadlc.models.user.OnlineUser;

import java.util.Optional;

public class ImplFadlcAPI extends FadlcAPI {
    @Override
    public IClaimManager getClaimManager() {
        return ClaimManager.getInstance();
    }

    @Override
    public IUserManager getUserManager() {
        return UserManager.getInstance();
    }

    @Override
    public IAdapter getAdapter() {
        return Adapter.getInstance();
    }

    @Override
    public boolean isActionAllowed(OnlineUser user, IPosition location, GroupSetting setting) {
        Optional<IClaim> claimAtLocation = getClaimManager().getClaimAt(location);
        if (claimAtLocation.isEmpty()) {
            return true;
        }

        if (claimAtLocation.get().getOwner().equals(user)) {
            return true;
        }

        IProfileGroup group = claimAtLocation.get().getProfile(location.getChunk()).orElseThrow().getPlayerGroup(user);

        if (group == null) {
            return false;
        }

        return group.getSettings().get(setting);
    }
}
