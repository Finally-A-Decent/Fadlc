package info.preva1l.fadlc.utils;

import lombok.experimental.UtilityClass;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@UtilityClass
public class FadlcExecutors {
    public final ExecutorService VIRTUAL_THREAD_PER_TASK = Executors.newVirtualThreadPerTaskExecutor();
}
