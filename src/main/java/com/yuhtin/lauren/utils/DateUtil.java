package com.yuhtin.lauren.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateUtil {

    public static String format(long time) {
        return "<t:" + time + ":R>";
    }

}

