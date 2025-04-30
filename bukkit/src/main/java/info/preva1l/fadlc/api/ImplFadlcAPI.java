package info.preva1l.fadlc.api;

import info.preva1l.fadlc.Fadlc;
import info.preva1l.fadlc.claim.IClaimService;
import info.preva1l.fadlc.claim.services.ClaimService;
import info.preva1l.fadlc.user.IUserService;
import info.preva1l.fadlc.user.UserService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ImplFadlcAPI extends FadlcAPI {
    private final Fadlc plugin;

    @Override
    public IClaimService getClaimManager() {
        return ClaimService.getInstance();
    }

    @Override
    public IUserService getUserManager() {
        return UserService.getInstance();
    }

    @Override
    public Adapter getAdapter() {
        return ImplAdapter.getInstance();
    }
}
