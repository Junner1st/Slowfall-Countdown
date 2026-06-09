package io.github.junner.slowfallcountdown.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ScheduledFuture;

public class DelayUtil {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static ScheduledFuture<?> currentTask = null;  // Track the current task

    public static void schedule(Runnable task, int delayMillis) {
        if (currentTask != null && !currentTask.isDone()) {
            currentTask.cancel(false);
        }

        // Schedule the new task
        currentTask = scheduler.schedule(task, delayMillis, TimeUnit.MILLISECONDS);
    }
}
