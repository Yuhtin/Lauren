package logger;

import application.Lauren;
import logger.controller.LoggerController;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Logger {

    public final String message;

    public void save() {
        if (Lauren.config.log)
            LoggerController.get().toFile(message);
    }

    public static Logger log(String message) {
        System.out.println(message);
        return new Logger(message);
    }
}
