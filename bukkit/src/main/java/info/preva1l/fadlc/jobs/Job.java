package info.preva1l.fadlc.jobs;

import info.preva1l.fadlc.config.Config;
import info.preva1l.fadlc.utils.Logger;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Represents a job that gets run on a thread.
 */
@RequiredArgsConstructor
public abstract class Job {
    private static final ScheduledExecutorService scheduler =
            new ScheduledThreadPoolExecutor(Config.getInstance().getJobs().getPoolSize());

    private final String name;
    private final Duration interval;
    private boolean silent = false;
    private ScheduledFuture<?> future;

    public Job(String name, Duration interval, boolean silent) {
        this.name = name;
        this.interval = interval;
        this.silent = silent;
    }

    public static Job of(String name, Runnable runnable, Duration interval) {
        return new Job(name, interval) {
            @Override
            protected void execute() {
                try {
                    runnable.run();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    public static Job of(String name, boolean silent, Runnable runnable, Duration interval) {
        return new Job(name, interval, silent) {
            @Override
            protected void execute() {
                try {
                    runnable.run();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    public final void start() {
        future = scheduler.scheduleAtFixedRate(this::run,
                interval.toMillis(), interval.toMillis(), TimeUnit.MILLISECONDS);
        Logger.info("[JOBS] Job '%s' scheduled at an interval of %s seconds".formatted(this.name, interval.get(ChronoUnit.SECONDS)));
    }

    void run() {
        int errors = 0;
        log("[JOBS] Running job '%s'".formatted(name));
        try {
            execute();
        } catch (Exception e) {
            e.printStackTrace();
            errors++;
        }
        log("[JOBS] Job '%s' completed (%s errors)".formatted(this.name, errors));
    }

    public final void shutdown() {
        future.cancel(true);
        log("[JOBS] Job '%s' shutdown!".formatted(this.name));
    }

    protected abstract void execute();

    private void log(String message) {
        if (!silent) {
            Logger.info(message);
        }
    }
}
