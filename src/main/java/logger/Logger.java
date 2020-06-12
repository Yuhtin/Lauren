package logger;

import application.Lauren;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Logger {

    public final String message;

    public void save() {
        if (Lauren.config.log)
            Lauren.logger.toFile(message);
    }

    public static Logger log(String message) {
        System.out.println(message);
        return new Logger(message);
    }
}
