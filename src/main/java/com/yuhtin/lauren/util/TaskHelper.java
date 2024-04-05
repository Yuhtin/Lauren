package com.yuhtin.lauren.util;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TaskHelper {
    private static final ExecutorService executor = Executors.newFixedThreadPool(128);

    public static void runTaskTimer(Runnable runnable, int delay, int period, TimeUnit timeFormat) {
        Timer timer = new Timer();
        new Thread(() -> timer.scheduleAtFixedRate(bind(runnable), timeFormat.toMillis(delay), timeFormat.toMillis(period))).start();
    }

    public static void runTaskTimerAsync(Runnable runnable, int delay, int period, TimeUnit timeFormat) {
        Timer timer = new Timer();
        runAsync(new Thread(() -> timer.scheduleAtFixedRate(bind(runnable), timeFormat.toMillis(delay), timeFormat.toMillis(period))));
    }

    public static void runTaskLater(Runnable runnable, int time, TimeUnit timeFormat) {
        Timer timer = new Timer();
        new Thread(() -> timer.schedule(bind(runnable), timeFormat.toMillis(time))).start();
    }

    public static void runTaskLaterAsync(Runnable runnable, int time, TimeUnit timeFormat) {
        Timer timer = new Timer();
        runAsync(new Thread(() -> timer.schedule(bind(runnable), timeFormat.toMillis(time))));
    }

    private static TimerTask bind(Runnable runnable) {
        return new TimerTask() {
            @Override
            public void run() {
                runnable.run();
            }
        };
    }

    public static void runAsync(Runnable runnable) { executor.execute(runnable); }
}