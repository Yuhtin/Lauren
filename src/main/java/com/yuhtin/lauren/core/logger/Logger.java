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

    public static Logger error(Exception exception) {
        return log(exception.getLocalizedMessage(), LogType.ERROR);
    }

    public static Logger log(Object message, LogType logType) {
        if (message == null) return new Logger("Generated a null content");

        StackTraceElement[] stackTrace = Utilities.INSTANCE.getStackTrace();
        String className = stackTrace[stackTrace.length > 3 ? 3 : 2].getFileName().replace(".java", "");

        LocalDateTime now = LocalDateTime.now();
        String hour = String.valueOf(now.getHour()),
                minute = String.valueOf(now.getMinute()),
                second = String.valueOf(now.getSecond());

        // fix time
        if (hour.length() == 1) hour = 0 + hour;
        if (minute.length() == 1) minute = 0 + minute;
        if (second.length() == 1) second = 0 + second;

        String time = "[" + hour + ":" + minute + ":" + second + "] ";

        message = time + logType.toString() + "> " + "[" + className + "] " + message.toString();

        System.out.println(message.toString());
        return new Logger(message.toString());
    }

    public static Logger log(String... message) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < message.length; i++) {
            if (i + 1 == message.length) builder.append(message[i]);
            else builder.append(message[i]).append("\n");
        }

        return log(builder.toString(), LogType.LOG);
    }

    public void save() {
        if (Lauren.config.log) LoggerController.get().toFile(message);
    }
}