package com.yuhtin.lauren.core.logger;

import com.yuhtin.lauren.Lauren;
import com.yuhtin.lauren.core.logger.controller.LoggerController;
import com.yuhtin.lauren.models.enums.LogType;
import com.yuhtin.lauren.utils.helper.Utilities;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
public class Logger {

    public final String message;

    public static void error(Exception exception) {
        log(exception.getLocalizedMessage(), LogType.ERROR);
    }

    public static void log(Object message, LogType logType) {
        if (message == null) message = "Generated a null content";

        StackTraceElement[] stackTrace = Utilities.INSTANCE.getStackTrace();
        String className = stackTrace[stackTrace.length > 3 ? 3 : 2].getFileName().replace(".java", "");

        LocalDateTime now = LocalDateTime.now();

        String hour = String.valueOf(now.getHour());
        String minute = String.valueOf(now.getMinute());
        String second = String.valueOf(now.getSecond());

        // fix time
        if (hour.length() == 1) hour = 0 + hour;
        if (minute.length() == 1) minute = 0 + minute;
        if (second.length() == 1) second = 0 + second;

        String time = "[" + hour + ":" + minute + ":" + second + "] ";

        message = time + logType.toString() + "> " + "[" + className + "] " + message.toString();

        System.out.println(message.toString());
        new Logger(message.toString()).save();
    }

    public static void log(String... message) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < message.length; i++) {
            if (i + 1 == message.length) builder.append(message[i]);
            else builder.append(message[i]).append("\n");
        }

        log(builder.toString(), LogType.LOG);
    }

    public void save() {
        if (Lauren.getInstance().getConfig().log) LoggerController.get().toFile(message);
    }
}