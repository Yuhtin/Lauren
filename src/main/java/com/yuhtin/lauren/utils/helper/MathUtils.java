package com.yuhtin.lauren.utils.helper;

import com.yuhtin.lauren.core.logger.Logger;

import java.util.concurrent.TimeUnit;

public class MathUtils {

    private MathUtils() {
        Logger.log("Unable to instantiate a utility class");
    }

    public static String format(long time) {
        String format = "";
        if (time < 0) return format;

        long hours = TimeUnit.MILLISECONDS.toHours(time);
        long hoursInMillis = TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(time - hoursInMillis);
        long minutesInMillis = TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(time - (hoursInMillis + minutesInMillis));
        int days = (int) (time / (1000 * 60 * 60 * 24));

        if (hours > 0) {
            if (days > 0) {
                time = time - TimeUnit.DAYS.toMillis(days);
                hours = TimeUnit.MILLISECONDS.toHours(time - minutesInMillis);
                String daysAndMonth;
                if (days >= 30) {
                    int months = days / 30;
                    int restDays = days - (30 * months);
                    daysAndMonth = plural(months, "mês", "meses") + (restDays > 0 ? ", " + plural(restDays, "dia", "dias") : "");
                } else daysAndMonth = plural(days, "dia", "dias");
                format = daysAndMonth + " e " + plural(hours, "hora", "horas");
                return format;
            } else format = plural(hours, "hora", "horas");
        }

        if (minutes > 0) {
            if ((seconds > 0) && (hours > 0))
                format += ", ";
            else if (hours > 0)
                format += " e ";
            format += plural(minutes, "minuto", "minutos");
        }

        if (seconds > 0) {
            if ((hours > 0) || (minutes > 0))
                format += " e ";
            format += plural(seconds, "segundo", "segundos");
        }

        if (format.equals("")) {
            long rest = time / 100;
            if (rest == 0)
                rest = 1;
            format = "0." + rest + "segundo";
        }

        if (days > 0) {
            format = plural(days, "dia", "dias");
        }
        return format;
    }

    public static int parseTime(String minutes) {
        try {
            return Integer.parseInt(minutes);
        } catch (Exception exception) {
            return 60;
        }
    }

    public static String plural(long x, String singular, String plural) {
        String date = String.valueOf(x);
        if (date.length() == 1) date = "0" + date;

        return date + " " + (x == 1 ? singular : plural);
    }
}
