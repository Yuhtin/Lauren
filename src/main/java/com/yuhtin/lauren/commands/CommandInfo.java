package com.yuhtin.lauren.commands;

import net.dv8tion.jda.api.Permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandInfo {

    String name();
    String description();

    /*
        Type [<argtype>arg]-Arg description for a not required arg
        Type <<argtype>arg>-Arg description for a required arg

        Arg types:
        # - Channel
        % - Role
        @ - Mention user
        ! - Integer
        Nothing - String
     */

    String[] args() default {};

    CommandType type();

    Permission[] permissions() default {};

}
