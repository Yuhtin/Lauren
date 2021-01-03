package com.yuhtin.lauren.core.logger;

import java.util.Calendar;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormat extends Formatter {

    @Override
    public String format(LogRecord record) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(record.getMillis());

        String time = calendar.get(Calendar.HOUR_OF_DAY) + ":"
                + calendar.get(Calendar.MINUTE) + ":"
                + calendar.get(Calendar.SECOND);

        return "[" + time + " " + record.getLevel().getName() + "] " +
                "[" + record.getSourceClassName() + "] " + record.getMessage();
    }

}
