package info.preva1l.fadlc.utils.metrics;

import info.preva1l.fadlc.Fadlc;
import info.preva1l.fadlc.managers.ClaimManager;
import info.preva1l.fadlc.utils.Logger;

/**
 * Created on 20/03/2025
 *
 * @author Preva1l
 */
public interface MetricsProvider {
    int METRICS_ID = 23412;

    default void setupMetrics() {
        Logger.info("Starting Metrics...");

        MetricsHolder.metrics = new Metrics(getPlugin(), METRICS_ID);
        MetricsHolder.metrics.addCustomChart(new Metrics.SingleLineChart("claims_created", () -> ClaimManager.getInstance().getAllClaims().size()));
        MetricsHolder.metrics.addCustomChart(new Metrics.SingleLineChart("chunks_claimed", () -> ClaimManager.getInstance().getClaimedChunks().size()));

        Logger.info("Metrics Logging Started!");
    }

    default void shutdownMetrics() {
        if (MetricsHolder.metrics != null) {
            MetricsHolder.metrics.shutdown();
        }
    }

    class MetricsHolder {
        private static Metrics metrics;
    }

    Fadlc getPlugin();
}
