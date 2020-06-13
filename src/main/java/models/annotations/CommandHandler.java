package models.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandHandler {

    String name();

    CommandType type();

    String description();

    enum CommandType {
        HELP(),
        UTILITY(),
        SCRIM(),
        CONFIG(),
        CUSTOM_MESSAGES(),
        SUPORT()
    }
}
