package com.yuhtin.lauren.commands;

import lombok.AllArgsConstructor;
import com.yuhtin.lauren.commands.CommandHandler;
import lombok.Data;

@AllArgsConstructor
@Data
public class RawCommand {

    private final String name;
    private final String description;
    private final CommandHandler.CommandType type;
    private final String[] aliases;
    private final Command command;

}
