package com.yuhtin.lauren.utils.helper;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class TaskHelper {
    public static void timer(TimerTask task, int delay, int period, TimeUnit timeFormat) {
        new Timer().scheduleAtFixedRate(task, timeFormat.toMillis(delay), timeFormat.toMillis(period));
    }

    public static void schedule(TimerTask task, int time, TimeUnit timeFormat) {
        new Timer().schedule(task, timeFormat.toMillis(time));
    }
}
