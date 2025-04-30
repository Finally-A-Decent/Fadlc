package info.preva1l.fadlc;

import info.preva1l.fadlc.claim.services.ClaimService;
import info.preva1l.fadlc.persistence.DataService;
import info.preva1l.fadlc.user.UserService;
import info.preva1l.trashcan.flavor.binder.FlavorBinderContainer;

/**
 * Created on 30/04/2025
 *
 * @author Preva1l
 */
public final class FadlcFlavorBinder extends FlavorBinderContainer {
    @Override
    public void populate() {
        bind(UserService.getInstance())
                .to(UserService.class)
                .bind();

        bind(ClaimService.getInstance())
                .to(ClaimService.class)
                .bind();

        bind(DataService.getInstance())
                .to(DataService.class)
                .bind();
    }
}
