package io.github.junner.slowfallcountdown.util;

import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.IntConsumer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ScheduledFuture;

public class DelayUtil {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final List<ScheduledFuture<?>> currentTasks = new ArrayList<>();

    public static synchronized void schedule(Runnable task, int delayMillis) {
        cancelCurrentTasks();

        currentTasks.add(scheduler.schedule(task, delayMillis, TimeUnit.MILLISECONDS));
    }

    public static synchronized void scheduleCountdown(
            BooleanSupplier isActive,
            IntConsumer countdownTask,
            Runnable completionTask,
            int totalSeconds,
            int countdownSeconds
    ) {
        cancelCurrentTasks();

        int normalizedCountdownSeconds = Math.min(Math.max(countdownSeconds, 0), totalSeconds);
        for (int remainingSeconds = normalizedCountdownSeconds; remainingSeconds > 0; remainingSeconds--) {
            int delaySeconds = totalSeconds - remainingSeconds;
            int messageSeconds = remainingSeconds;
            currentTasks.add(scheduler.schedule(
                    () -> executeIfActive(isActive, () -> countdownTask.accept(messageSeconds)),
                    delaySeconds,
                    TimeUnit.SECONDS
            ));
        }

        currentTasks.add(scheduler.schedule(
                () -> executeIfActive(isActive, completionTask),
                totalSeconds,
                TimeUnit.SECONDS
        ));
    }

    private static synchronized void cancelCurrentTasks() {
        for (ScheduledFuture<?> task : currentTasks) {
            if (!task.isDone()) {
                task.cancel(false);
            }
        }
        currentTasks.clear();
    }

    private static void executeIfActive(BooleanSupplier isActive, Runnable task) {
        MinecraftClient client = MinecraftClient.getInstance();
        client.execute(() -> {
            if (!isActive.getAsBoolean()) {
                cancelCurrentTasks();
                return;
            }

            task.run();
        });
    }
}
