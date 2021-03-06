package com.yuhtin.lauren.models.annotations;

import lombok.AllArgsConstructor;
import lombok.Getter;

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

    String[] alias();

    @AllArgsConstructor
    enum CommandType {
        HELP("Ajuda"),
        MUSIC("Música"),
        UTILITY("Utilidade"),
        SCRIM("Scrim"),
        CONFIG("Configurações"),
        CUSTOM_MESSAGES("Mensagens Customizadas"),
        ADMIN("Admin"),
        OTHER("Outros");

        @Getter private final String name;
    }
}
