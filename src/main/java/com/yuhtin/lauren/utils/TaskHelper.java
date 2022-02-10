package com.yuhtin.lauren.utils;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TaskHelper {
    private static final ExecutorService executor = Executors.newFixedThreadPool(128);

    public static void runTaskTimer(TimerTask task, int delay, int period, TimeUnit timeFormat) {
        Timer timer = new Timer();
        new Thread(() -> timer.scheduleAtFixedRate(task, timeFormat.toMillis(delay), timeFormat.toMillis(period))).start();
    }

    public static void runTaskTimerAsync(TimerTask task, int delay, int period, TimeUnit timeFormat) {
        Timer timer = new Timer();
        runAsync(new Thread(() -> timer.scheduleAtFixedRate(task, timeFormat.toMillis(delay), timeFormat.toMillis(period))));
    }

    public static void runTaskLater(TimerTask task, int time, TimeUnit timeFormat) {
        Timer timer = new Timer();
        new Thread(() -> timer.schedule(task, timeFormat.toMillis(time))).start();
    }

    public static void runTaskLaterAsync(TimerTask task, int time, TimeUnit timeFormat) {
        Timer timer = new Timer();
        runAsync(new Thread(() -> timer.schedule(task, timeFormat.toMillis(time))));
    }

    public static void runAsync(Runnable runnable) { executor.execute(runnable); }
}
