package info.preva1l.fadlc.metrics;

import info.preva1l.fadlc.Fadlc;
import info.preva1l.fadlc.claim.services.ClaimService;
import info.preva1l.trashcan.flavor.annotations.Close;
import info.preva1l.trashcan.flavor.annotations.Configure;
import info.preva1l.trashcan.flavor.annotations.Service;
import info.preva1l.trashcan.flavor.annotations.inject.Inject;

import java.util.logging.Logger;

/**
 * Created on 20/03/2025
 *
 * @author Preva1l
 */
@Service
public final class MetricsService {
    public static final MetricsService instance = new MetricsService();

    private static final int METRICS_ID = 23412;

    @Inject private Fadlc plugin;
    @Inject private Logger logger;

    private Metrics metrics;

    @Configure
    public void configure() {
        logger.info("Starting Metrics...");

        metrics = new Metrics(plugin, METRICS_ID);
        metrics.addCustomChart(new Metrics.SingleLineChart("claims_created", () -> ClaimService.getInstance().getAllClaims().size()));
        metrics.addCustomChart(new Metrics.SingleLineChart("chunks_claimed", () -> ClaimService.getInstance().getClaimedChunks().size()));

//        metrics.addCustomChart(new Metrics.SimplePie("database_type", () -> Config.i().getStorage().getType().getFriendlyName()));
//        metrics.addCustomChart(new Metrics.SimplePie(
//                "multi_server",
//                () -> Config.i().getBroker().isEnabled() ? Config.i().getBroker().getType().getDisplayName() : "None"
//        ));


        logger.info("Metrics Logging Started!");
    }

    @Close
    public void close() {
        if (metrics != null) {
            metrics.shutdown();
        }
    }
}
