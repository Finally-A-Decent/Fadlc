package info.preva1l.fadlc.jobs;

import info.preva1l.trashcan.flavor.annotations.Close;
import info.preva1l.trashcan.flavor.annotations.Configure;

/**
 * Created on 20/03/2025
 *
 * @author Preva1l
 */
public final class JobService {
    public static final JobService instance = new JobService();

    private ClaimBorderJob borderJob;

    @Configure
    public void load() {
        SaveJobs.startAll();
        borderJob = new ClaimBorderJob();
        borderJob.start();
    }

    @Close
    public void shutdown() {
        SaveJobs.forceRunAll();
        SaveJobs.shutdownAll();

        if (borderJob != null) {
            borderJob.shutdown();
        }
    }
}
