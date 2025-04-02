package info.preva1l.fadlc.utils;

import lombok.experimental.UtilityClass;

import java.util.concurrent.ExecutorService;

@UtilityClass
public class Executors {
    public final ExecutorService V_THREAD_P_TASK = java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor();
    public final ExecutorService VTHREAD = V_THREAD_P_TASK;
}
