package com.yuhtin.lauren.utils.helper;

import com.yuhtin.lauren.core.logger.Logger;

public class MathUtils {

    private MathUtils() {
        Logger.log("Unable to instantiate a utility class");
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
