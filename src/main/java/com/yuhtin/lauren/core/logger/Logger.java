package com.yuhtin.lauren.core.logger;

import com.yuhtin.lauren.Lauren;
import com.yuhtin.lauren.core.logger.controller.LoggerController;
import com.yuhtin.lauren.models.enums.LogType;
import com.yuhtin.lauren.utils.helper.Utilities;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Logger {

    public final String message;

    public static Logger error(Exception exception) {
        return log(exception.getLocalizedMessage(), LogType.ERROR);
    }

    public static Logger log(Object message, LogType logType) {
        if (message == null) return new Logger("Generated a null content");

        message = logType.toString() + "> " + message.toString();

        System.out.println(message.toString());
        return new Logger(message.toString());
    }

    public static Logger log(String... message) {
        StringBuilder builder = new StringBuilder();
        for (String line : message) {
            builder.append(line).append("\n");
        }

        return log(builder.toString());
    }

    public static Logger log(Object message) {
        return log("[" + Utilities.INSTANCE.getStackTrace()[2].getFileName().replace(".java", "") + "] " + message, LogType.LOG);
    }

    public void save() {
        if (Lauren.config.log) LoggerController.get().toFile(message);
    }
}