package com.yuhtin.lauren.utils.helper;

import java.util.concurrent.TimeUnit;

public class MathUtils {
    public static String format(long time) {
        String format = "";
        if (time < 0) return format;
        System.out.println(time);

        long hours = TimeUnit.MILLISECONDS.toHours(time);
        long hoursInMillis = TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(time - hoursInMillis);
        long minutesInMillis = TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(time - (hoursInMillis + minutesInMillis));
        int days = (int) (time / (1000 * 60 * 60 * 24));
        if (hours > 0)
            if (days > 0) {
                time = time - TimeUnit.DAYS.toMillis(days);
                hours = TimeUnit.MILLISECONDS.toHours(time - minutesInMillis);
                String daysAndMonth;
                if (days >= 30) {
                    int months = days / 30;
                    int restDays = days - (30 * months);
                    daysAndMonth = plural(months, "mês", "meses") + (restDays > 0 ? ", " + plural(restDays, "dia", "dias") : "");
                } else daysAndMonth = plural(days, "dia", "dias");
                format = daysAndMonth + ", " + plural(hours, "hora", "horas");
                return format;
            } else {
                format = plural(hours, "hora", "horas");
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

    public static String bytesToLegibleValue(double bytes) {
        if (bytes < 1024 * 1024)
            return String.format("%.2f KB", bytes);
        else if (bytes < Math.pow(2, 20) * 1024)
            return String.format("%.2f MB", bytes / Math.pow(2, 20));
        else if (bytes < Math.pow(2, 30) * 1024)
            return String.format("%.2f GB", bytes / Math.pow(2, 30));
        else if (bytes < Math.pow(2, 40) * 1024)
            return String.format("%.2f TB", bytes / Math.pow(2, 40));
        else
            return "N/A (1TB?)";
    }

    public static int parseTime(String minutes) {
        try {
            return Integer.parseInt(minutes);
        } catch (Exception exception) {
            return 60;
        }
    }

    public static String plural(long x, String singular, String plural) {
        return x + " " + (x == 1 ? singular : plural);
    }
}
