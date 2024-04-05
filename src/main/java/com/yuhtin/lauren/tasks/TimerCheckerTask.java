package com.yuhtin.lauren.tasks;

import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.manager.TimerManager;
import com.yuhtin.lauren.util.TaskHelper;
import lombok.val;

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

    private final TimerManager timerManager;
    private final Logger logger;

    public TimerCheckerTask(TimerManager timerManager, Logger logger) {
        this.timerManager = timerManager;
        this.logger = logger;

        CALENDAR.setTimeZone(TimeZone.getTimeZone(ZoneId.of("America/Sao_Paulo")));
    }

    private final String[] week = {"Domingo", "Segunda", "Terça", "Quarta", "Quinta", "Sexta", "Sábado"};

    @Override
    public void run() {

        CALENDAR.setTimeInMillis(System.currentTimeMillis());

        val weekDay = week[CALENDAR.get(Calendar.DAY_OF_WEEK) - 1].toLowerCase();
        val time = CALENDAR.get(Calendar.HOUR_OF_DAY) + ":" + CALENDAR.get(Calendar.MINUTE);

        for (val timer : this.timerManager.getTimers()) {
            val timerTime = timer.hour() + ":" + timer.minute();

            if (!timer.day().equalsIgnoreCase("ALL")
                    && !timer.day().equalsIgnoreCase(weekDay)
                    || !time.equalsIgnoreCase(timerTime)) continue;

            logger.info("Running " + timer.name());
            TaskHelper.runAsync(timer::run);
        }

    }

}
