package info.preva1l.fadlc.api;

import info.preva1l.fadlc.managers.IClaimManager;
import info.preva1l.fadlc.managers.IUserManager;
import info.preva1l.fadlc.models.IPosition;
import info.preva1l.fadlc.models.claim.settings.GroupSetting;
import info.preva1l.fadlc.models.user.OnlineUser;
import lombok.Getter;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public abstract class FadlcAPI {
    /**
     * -- GETTER --
     *  Get the instance of the Fadlc API.
     */
    @Getter private static FadlcAPI instance;

    /**
     * Get the claim manager to interact with claims.
     * @return the claim manager instance.
     */
    public abstract IClaimManager getClaimManager();

    /**
     * Get the user manager to interact with users.
     * @return the user manager instance.
     */
    public abstract IUserManager getUserManager();

    /**
     * The adapter to adapt bukkit types/values to Fadlc objects.
     * <p>
     *     Example: {@link org.bukkit.Location} -> {@link IPosition}
     * </p>
     * @return the adapter instance.
     */
    public abstract IAdapter getAdapter();

    /**
     * Check if the player can perform an action at the provided location.
     *
     * @param user the player to check.
     * @param location the location of the action.
     * @param setting the action to check
     * @return true if the action is allowed false if not
     */
    public abstract boolean isActionAllowed(OnlineUser user, IPosition location, GroupSetting setting);

    @ApiStatus.Internal
    public static void setInstance(@NotNull FadlcAPI newInstance) {
        if (instance != null) {
            throw new IllegalStateException("FadlcAPI instance already set!");
        }
        instance = newInstance;
    }
}
