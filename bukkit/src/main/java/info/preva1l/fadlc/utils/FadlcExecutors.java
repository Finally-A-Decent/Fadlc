package info.preva1l.fadlc.utils;

import info.preva1l.fadlc.Fadlc;
import org.bukkit.Bukkit;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FadlcExecutors {
    public static final ExecutorService VIRTUAL_THREAD_POOL = Executors.newVirtualThreadPerTaskExecutor();
    public static final Executor MAIN_THREAD = Bukkit.getScheduler().getMainThreadExecutor(Fadlc.i());
}
