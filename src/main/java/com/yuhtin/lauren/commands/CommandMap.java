package com.yuhtin.lauren.commands;

import lombok.Data;
import net.dv8tion.jda.api.JDA;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
@Data
public final class CommandMap {

    private final JDA bot;
    private final String prefix;

    private Map<String, Command> commands = new HashMap<>();

    public void register(String key, Command value, String... aliases) {
        if (!key.startsWith(prefix)) key = prefix + key;

        commands.put(key, value);
        bot.upsertCommand(key, value.getDescription()).queue();

        for (String alias : aliases) {

            if (!alias.startsWith(prefix)) alias = prefix + alias;
            commands.put(alias, value);
            bot.upsertCommand(alias, value.getDescription()).queue();

        }

    }

}
