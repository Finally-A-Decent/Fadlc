package info.preva1l.fadlc.utils;

import lombok.experimental.UtilityClass;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

@UtilityClass
public class Executors {
    public final ExecutorService V_THREAD_P_TASK = java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor();
    public final ExecutorService VTHREAD = V_THREAD_P_TASK;

    public final ScheduledExecutorService SCHEDULED = java.util.concurrent.Executors.newSingleThreadScheduledExecutor((r) -> {
        var t = new Thread(r, "FadlcScheduler");
        t.setDaemon(true);
        return t;
    });
}
