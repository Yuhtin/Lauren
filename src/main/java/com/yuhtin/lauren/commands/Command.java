package com.yuhtin.lauren.commands;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Command {

    private final String name;
    private final String description;
    private final CommandHandler.CommandType type;
    private final String[] aliases;
    private final CommandExecutor commandExecutor;

}
