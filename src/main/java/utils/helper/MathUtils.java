package utils.helper;

import java.util.concurrent.TimeUnit;

public class MathUtils {
    public static String format(long time) {
        time = System.currentTimeMillis() - time;
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
}
