package com.yuhtin.lauren.commands;

import lombok.Data;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

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

    private Map<String, RawCommand> commands = new HashMap<>();

    public void register(String key, RawCommand value, String... aliases) {
        if (!key.startsWith(prefix)) key = prefix + key;

        commands.put(key, value);

        for (String alias : aliases) {

            if (!alias.startsWith(prefix)) alias = prefix + alias;
            commands.put(alias, value);

        }

        bot.upsertCommand(key, value.)

    }

}
