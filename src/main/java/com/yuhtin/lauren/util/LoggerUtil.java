package com.yuhtin.lauren.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.logging.Logger;

public class LoggerUtil {

    public static Logger getLogger() {
        return Logger.getLogger("Lauren");
    }

    public static void printException(Throwable exception) {
        Writer buffer = new StringWriter();

        PrintWriter pw = new PrintWriter(buffer);
        exception.printStackTrace(pw);

        getLogger().severe(buffer.toString());
    }

}
