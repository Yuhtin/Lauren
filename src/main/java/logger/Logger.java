package logger;

import application.Lauren;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Logger {

    @Getter
    private final String message;

    public void save() {
        Lauren.logger.toFile(message);
    }

    public static Logger log(String message) {
        System.out.println(message);
        return new Logger(message);
    }
}
