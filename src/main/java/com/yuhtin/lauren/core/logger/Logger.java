package com.yuhtin.lauren.core.logger;

import com.yuhtin.lauren.application.Lauren;
import com.yuhtin.lauren.core.logger.controller.LoggerController;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Logger {

    public final String message;

    public void save() {
        if (Lauren.config.log)
            LoggerController.get().toFile(message);
    }

    public static Logger error(Exception exception) {
        exception.printStackTrace();
        return new Logger(exception.getMessage());
    }

    public static Logger log(String message) {
        System.out.println(message);
        return new Logger(message);
    }
}
