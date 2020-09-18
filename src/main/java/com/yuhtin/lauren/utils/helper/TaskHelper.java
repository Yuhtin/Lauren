package com.yuhtin.lauren.utils.helper;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TaskHelper {
    private static final ExecutorService executor = Executors.newFixedThreadPool(128);

    public static void timer(TimerTask task, int delay, int period, TimeUnit timeFormat) {
        Timer timer = new Timer();
        new Thread(() -> timer.scheduleAtFixedRate(task, timeFormat.toMillis(delay), timeFormat.toMillis(period))).start();
    }

    public static void schedule(TimerTask task, int time, TimeUnit timeFormat) {
        Timer timer = new Timer();
        new Thread(() -> timer.schedule(task, timeFormat.toMillis(time))).start();
    }

    public static void runAsync(Runnable runnable) { executor.execute(runnable); }
}
