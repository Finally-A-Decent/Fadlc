package info.preva1l.fadlc.api;

import info.preva1l.fadlc.Fadlc;
import info.preva1l.fadlc.managers.ClaimManager;
import info.preva1l.fadlc.managers.IClaimManager;
import info.preva1l.fadlc.managers.IUserManager;
import info.preva1l.fadlc.managers.UserManager;
import info.preva1l.fadlc.models.IPosition;
import info.preva1l.fadlc.models.claim.settings.GroupSetting;
import info.preva1l.fadlc.models.user.OnlineUser;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ImplFadlcAPI extends FadlcAPI {
    private final Fadlc plugin;

    @Override
    public IClaimManager getClaimManager() {
        return ClaimManager.getInstance();
    }

    @Override
    public IUserManager getUserManager() {
        return UserManager.getInstance();
    }

    @Override
    public Adapter getAdapter() {
        return ImplAdapter.getInstance();
    }

    @Override
    public boolean isActionAllowed(OnlineUser user, IPosition location, GroupSetting setting) {
        return plugin.isActionAllowed(user, location, setting);
    }
}
