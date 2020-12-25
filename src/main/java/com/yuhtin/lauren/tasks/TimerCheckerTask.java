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

    @Override
    public void run() {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone(ZoneId.of("America/Sao_Paulo")));

        String weekDay = week[calendar.get(Calendar.DAY_OF_WEEK)].toLowerCase();
        String time = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);

        TimerManager instance = TimerManager.getInstance();
        for (Timer timer : instance.getTimers()) {

            String timerTime = timer.hour() + ":" + timer.minute();

            if ((!timer.day().equalsIgnoreCase("ALL")
                    && !timer.day().equalsIgnoreCase(weekDay))
                    || !time.equalsIgnoreCase(timerTime)) continue;

            Logger.log("Running " + timer.name());
            TaskHelper.runAsync(timer::run);

        }

    }

}
