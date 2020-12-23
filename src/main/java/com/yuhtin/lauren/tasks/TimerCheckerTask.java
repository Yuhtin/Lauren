package com.yuhtin.lauren.tasks;

import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.models.manager.TimerManager;
import com.yuhtin.lauren.timers.Timer;
import com.yuhtin.lauren.utils.helper.TaskHelper;

import java.time.ZoneId;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.TimerTask;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class TimerCheckerTask extends TimerTask {

    private final String[] week = {"Sábado", "Domingo", "Segunda", "Terça", "Quarta", "Quinta", "Sexta"};
    private final Calendar calendar = Calendar.getInstance();

    @Override
    public void run() {

        String weekDay = week[calendar.get(Calendar.DAY_OF_WEEK)].toLowerCase();
        Logger.log("Actual day: " + weekDay);

        TimerManager instance = TimerManager.getInstance();
        for (Timer timer : instance.getTimers()) {

            if (!timer.day().equalsIgnoreCase(weekDay)
                    || timer.hour() != calendar.get(Calendar.HOUR_OF_DAY)
                    || timer.minute() != calendar.get(Calendar.MINUTE)) return;

            TaskHelper.runAsync(timer::run);

        }

    }

    public void updateCalendar() {
        calendar.setTimeZone(TimeZone.getTimeZone(ZoneId.of("America/Sao_Paulo")));
    }

}
