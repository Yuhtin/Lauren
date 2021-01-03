package com.yuhtin.lauren.utils.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MathUtils {

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
