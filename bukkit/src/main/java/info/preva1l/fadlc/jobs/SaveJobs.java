package info.preva1l.fadlc.jobs;

import info.preva1l.fadlc.config.Config;
import info.preva1l.fadlc.managers.ClaimManager;
import info.preva1l.fadlc.managers.PersistenceManager;
import info.preva1l.fadlc.managers.UserManager;
import info.preva1l.fadlc.models.IClaimChunk;
import info.preva1l.fadlc.models.claim.IClaim;
import info.preva1l.fadlc.models.claim.IClaimProfile;
import info.preva1l.fadlc.models.claim.IProfileGroup;
import info.preva1l.fadlc.models.user.OnlineUser;

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
            ClaimManager.getInstance().getAllClaims().forEach(f -> {
                    PersistenceManager.getInstance().save(IClaim.class, f).join();
                    f.getProfiles().values().forEach(p -> {
                        PersistenceManager.getInstance().save(IClaimProfile.class, p).join();
                        p.getGroups().values().forEach(g -> PersistenceManager.getInstance().save(IProfileGroup.class, g).join());
                    });
                    f.getClaimedChunks().keySet().forEach(cUUID ->
                            PersistenceManager.getInstance().save(IClaimChunk.class, ClaimManager.getInstance().getChunk(cUUID)).join());
            });
        }
    }

    public static class UsersSaveJob extends Job {
        public UsersSaveJob() {
            super("Users Save", Duration.ofMinutes(Config.i().getJobs().getUsersSaveInterval()));
        }

        @Override
        protected void execute() {
            UserManager.getInstance().getAllUsers().forEach(f ->
                    PersistenceManager.getInstance().save(OnlineUser.class, f).join());
        }
    }
}
