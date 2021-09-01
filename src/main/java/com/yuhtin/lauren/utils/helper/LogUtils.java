package com.yuhtin.lauren.utils.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LogUtils {

    public static StackTraceElement[] getStackTrace() {
        Throwable throwable = new Throwable();
        throwable.fillInStackTrace();

        return throwable.getStackTrace();
    }

}
