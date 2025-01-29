package info.preva1l.fadlc.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FadlcExecutors {
    public static final ExecutorService VIRTUAL_THREAD_POOL = Executors.newVirtualThreadPerTaskExecutor();
}
