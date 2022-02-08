package com.yuhtin.lauren.commands;

import lombok.Data;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
@Data
public final class CommandMap {

    @Getter private final Map<String, Command> commands = new HashMap<>();

    public void register(String key, Command value) {
        commands.put(key, value);
    }

}
