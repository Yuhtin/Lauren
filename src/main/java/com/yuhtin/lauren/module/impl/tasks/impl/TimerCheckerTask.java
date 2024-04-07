package com.yuhtin.lauren.module.impl.tasks.impl;

import com.yuhtin.lauren.module.Module;
import com.yuhtin.lauren.module.impl.timer.Timer;
import com.yuhtin.lauren.module.impl.timer.TimerModule;
import com.yuhtin.lauren.util.TaskHelper;

import java.time.ZoneId;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.TimerTask;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

public class TimerCheckerTask extends TimerTask {

    private static final Calendar CALENDAR = Calendar.getInstance();

    static {
        CALENDAR.setTimeZone(TimeZone.getTimeZone(ZoneId.of("America/Sao_Paulo")));
    }

    private final String[] week = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

    @Override
    public void run() {
        TimerModule timerModule = Module.instance(TimerModule.class);
        if (timerModule == null) return;

        CALENDAR.setTimeInMillis(System.currentTimeMillis());
        String currentHourAndMinute = CALENDAR.get(Calendar.HOUR_OF_DAY) + ":" + CALENDAR.get(Calendar.MINUTE);

        for (Timer timer : timerModule.getTimers()) {
            if (!isToday(timer)) continue;
            if (timer.hours().isEmpty()) continue;

            for (String hour : timer.hours()) {
                if (!hour.equalsIgnoreCase(currentHourAndMinute)) continue;
                TaskHelper.runAsync(timer::run);
            }
        }
    }

    private boolean isToday(Timer timer) {
        String currentWeekDay = week[CALENDAR.get(Calendar.DAY_OF_WEEK) - 1].toLowerCase();

        return timer.day().equalsIgnoreCase("ALL")
                || timer.day().equalsIgnoreCase(currentWeekDay);
    }

}
