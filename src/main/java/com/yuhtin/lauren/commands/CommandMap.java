package com.yuhtin.lauren.commands;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
@Data
public final class CommandMap {

    private final String prefix;

    private Map<String, com.yuhtin.supremo.ticketbot.command.Command> commands = new HashMap<>();

    public void register(String key, com.yuhtin.supremo.ticketbot.command.Command value, String... aliases) {
        if (!key.startsWith(prefix)) key = prefix + key;

        commands.put(key, value);

        for (String alias : aliases) {

            if (!alias.startsWith(prefix)) alias = prefix + alias;
            commands.put(alias, value);

        }

    }

}
