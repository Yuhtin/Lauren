package com.yuhtin.lauren.core.logger;

import com.yuhtin.lauren.application.Lauren;
import com.yuhtin.lauren.core.logger.controller.LoggerController;
import com.yuhtin.lauren.models.enums.LogType;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Logger {

    public final String message;

    public static Logger error(Exception exception) {
        return log(exception.getMessage(), LogType.ERROR);
    }

    public static Logger log(String message, LogType logType) {
        message = logType.toString() + "> " + message;

        System.out.println(message);
        return new Logger(message);
    }

    public static Logger log(String message) { return log(message, LogType.LOG); }

    public void save() {
        if (Lauren.config.log)
            LoggerController.get().toFile(message);
    }
}
