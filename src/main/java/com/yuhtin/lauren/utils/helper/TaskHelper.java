package com.yuhtin.lauren.utils.helper;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class TaskHelper {
    public static void timer(TimerTask task, long delay, long period) {
        new Timer().scheduleAtFixedRate(task, delay, period);
    }

    public static void schedule(TimerTask task, long time) {
        new Timer().schedule(task, time);
    }
}
