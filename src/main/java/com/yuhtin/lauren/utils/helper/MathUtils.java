package com.yuhtin.lauren.utils.helper;

import java.util.concurrent.TimeUnit;

public class MathUtils {
    public static String format(long time) {
        String format = "";
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
                format = days + " dias, " + hours + (hours > 1 ? " horas" : " hora");
                return format;
            } else {
                format = hours + (hours > 1 ? " horas" : " hora");
            }
        if (minutes > 0) {
            if ((seconds > 0) && (hours > 0))
                format += ", ";
            else if (hours > 0)
                format += " e ";
            format += minutes + (minutes > 1 ? " minutos" : " minuto");
        }
        if (seconds > 0) {
            if ((hours > 0) || (minutes > 0))
                format += " e ";
            format += seconds + (seconds > 1 ? " segundos" : " segundo");
        }
        if (format.equals("")) {
            long rest = time / 100;
            if (rest == 0)
                rest = 1;
            format = "0." + rest + " segundo";
        }
        if (days > 0) {
            format = days + " dias";
        }
        return format;
    }

    public static String bytesToLegibleValue(double bytes) {
        if (bytes < 1024 * 1024)
            return String.format("%.2f KB", bytes);
        else if (bytes < Math.pow(2, 20) * 1024)
            return String.format("%.2f MB", bytes / Math.pow(2, 20));
        else if (bytes < Math.pow(2, 30) * 1024 )
            return String.format("%.2f GB", bytes / Math.pow(2, 30));
        else if (bytes < Math.pow(2, 40) * 1024)
            return String.format("%.2f TB", bytes / Math.pow(2, 40));
        else
            return "N/A (1TB?)";
    }

    public static int parseTime(String minutes) {
        try {
            return Integer.parseInt(minutes);
        }catch (Exception exception) {
            return 60;
        }
    }

    /**
     * The methods below are from:
     *
     * @author John Grosh (john.a.grosh@gmail.com)
     */

    public static String plural(long x, String singular, String plural) {
        return x == 1 ? singular : plural;
    }

    public static String secondsToTime(long timeseconds) {
        StringBuilder builder = new StringBuilder();
        int years = (int) (timeseconds / (60 * 60 * 24 * 365));
        if (years > 0) {
            builder.append("**").append(years).append("** ").append(plural(years, "ano", "anos")).append(", ");
            timeseconds = timeseconds % (60 * 60 * 24 * 365);
        }
        int weeks = (int) (timeseconds / (60 * 60 * 24 * 7));
        if (weeks > 0) {
            builder.append("**").append(weeks).append("** ").append(plural(weeks, "mês", "messes")).append(", ");
            timeseconds = timeseconds % (60 * 60 * 24 * 7);
        }
        int days = (int) (timeseconds / (60 * 60 * 24));
        if (days > 0) {
            builder.append("**").append(days).append("** ").append(plural(days, "dia", "dias")).append(", ");
            timeseconds = timeseconds % (60 * 60 * 24);
        }
        int hours = (int) (timeseconds / (60 * 60));
        if (hours > 0) {
            builder.append("**").append(hours).append("** ").append(plural(hours, "hora", "horas")).append(", ");
            timeseconds = timeseconds % (60 * 60);
        }
        int minutes = (int) (timeseconds / (60));
        if (minutes > 0) {
            builder.append("**").append(minutes).append("** ").append(plural(minutes, "minuto", "minutos")).append(", ");
            timeseconds = timeseconds % (60);
        }
        if (timeseconds > 0) {
            builder.append("**").append(timeseconds).append("** ").append(plural(timeseconds, "segundo", "segundos"));
        }
        String str = builder.toString();
        if (str.endsWith(", "))
            str = str.substring(0, str.length() - 2);
        if (str.equals(""))
            str = "**Finalizado**";
        return str;
    }
}
