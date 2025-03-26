package info.preva1l.fadlc.jobs;

import info.preva1l.fadlc.utils.Logger;

/**
 * Created on 20/03/2025
 *
 * @author Preva1l
 */
public interface JobProvider {
    default void loadJobs() {
        Logger.info("Starting Jobs...");
        SaveJobs.startAll();
        JobHolder.self.borderJob = new ClaimBorderJob();
        JobHolder.self.borderJob.start();
        Logger.info("Jobs Started!");
    }

    default void shutdownJobs() {
        SaveJobs.forceRunAll();
        SaveJobs.shutdownAll();

        if (JobHolder.self.borderJob != null) {
            JobHolder.self.borderJob.shutdown();
        }
    }

    class JobHolder {
        private static final JobHolder self = new JobHolder();
        private ClaimBorderJob borderJob;
    }
}
