package info.preva1l.fadlc.jobs;

import info.preva1l.fadlc.claim.IClaim;
import info.preva1l.fadlc.claim.IClaimChunk;
import info.preva1l.fadlc.claim.IClaimProfile;
import info.preva1l.fadlc.claim.IProfileGroup;
import info.preva1l.fadlc.claim.services.ClaimService;
import info.preva1l.fadlc.config.Config;
import info.preva1l.fadlc.persistence.DataService;
import info.preva1l.fadlc.user.OnlineUser;
import info.preva1l.fadlc.user.UserService;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class SaveJobs {
    private static final List<Job> runningJobs = new ArrayList<>();

    public static void startAll() {
        Stream.of(
            new ClaimSaveJob(),
            new UsersSaveJob()
        ).forEach(job -> {
            job.start();
            runningJobs.add(job);
        });
    }

    public static void forceRunAll() {
        runningJobs.forEach(Job::run);
    }

    public static void shutdownAll() {
        runningJobs.forEach(Job::shutdown);
    }

    public static class ClaimSaveJob extends Job {
        public ClaimSaveJob() {
            super("Claim Save", Duration.ofMinutes(Config.i().getJobs().getClaimSaveInterval()));
        }

        @Override
        protected void execute() {
            ClaimService.getInstance().getAllClaims().forEach(f -> {
                    DataService.getInstance().save(IClaim.class, f).join();
                    f.getProfiles().values().forEach(p -> {
                        DataService.getInstance().save(IClaimProfile.class, p).join();
                        p.getGroups().values().forEach(g -> DataService.getInstance().save(IProfileGroup.class, g).join());
                    });
                    f.getClaimedChunks().keySet().forEach(cUUID ->
                            DataService.getInstance().save(IClaimChunk.class, ClaimService.getInstance().getChunk(cUUID)).join());
            });
        }
    }

    public static class UsersSaveJob extends Job {
        public UsersSaveJob() {
            super("Users Save", Duration.ofMinutes(Config.i().getJobs().getUsersSaveInterval()));
        }

        @Override
        protected void execute() {
            UserService.getInstance().getAllUsers().forEach(f ->
                    DataService.getInstance().save(OnlineUser.class, f).join());
        }
    }
}
