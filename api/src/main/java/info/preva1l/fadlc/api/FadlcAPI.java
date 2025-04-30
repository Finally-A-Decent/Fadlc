package info.preva1l.fadlc.api;

import info.preva1l.fadlc.claim.IClaimService;
import info.preva1l.fadlc.models.IPosition;
import info.preva1l.fadlc.user.IUserService;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * The Fadlc API allowing access and modification to every aspect of the plugin.
 * <br><br>
 * Created on 10/09/2024
 *
 * @author Preva1l
 */
@ApiStatus.NonExtendable
public abstract class FadlcAPI {
    private static FadlcAPI instance = null;

    /**
     * Only Fadlc should initialize the API.
     */
    FadlcAPI() {}

    /**
     * Get the claim manager to interact with claims.
     *
     * @return the claim manager instance.
     * @since 1.0.0
     */
    public abstract IClaimService getClaimManager();

    /**
     * Get the user manager to interact with users.
     *
     * @return the user manager instance.
     * @since 1.0.0
     */
    public abstract IUserService getUserManager();

    /**
     * The adapter to adapt bukkit types/values to Fadlc objects.
     * <p>
     *     Example: {@link org.bukkit.Location} -> {@link IPosition}
     * </p>
     *
     * @return the adapter instance.
     * @since 1.0.0
     */
    public abstract Adapter getAdapter();

    /**
     * Get the instance of the Fadlc API.
     *
     * @return the Fadlc API implementation.
     * @throws IllegalStateException if the API is accessed incorrectly.
     */
    public static FadlcAPI getInstance() {
        if (instance == null) {
            throw new IllegalStateException(
                    """
                    The Fadlc API is not initialized yet!
                    This could be a couple things:
                    1. You are not depending/soft-depending on Fadlc
                       - Check your plugin.yml
                    2. You are trying to access the API before onEnable
                       - Check for usages of FadlcAPI#getInstance() in static constructors or in onLoad
                    3. You are shading/implementing the API instead of compiling against it
                       - If your using gradle make sure your using compileOnly instead of implementation
                       - If your using maven make sure your dependency is declared as provided
                    """.stripIndent());
        }
        return instance;
    }

    /**
     * Set the instance of the Fadlc API.
     *
     * @param newInstance the instance to set the api to.
     * @throws IllegalStateException if the instance is already assigned.
     */
    @ApiStatus.Internal
    public static void setInstance(@NotNull FadlcAPI newInstance) {
        if (instance != null) {
            throw new IllegalStateException("Instance has already been set");
        }
        instance = newInstance;
    }
}
