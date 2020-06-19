package com.yuhtin.lauren.core;

import lombok.AllArgsConstructor;
import com.yuhtin.lauren.models.annotations.CommandHandler;

@AllArgsConstructor
public class RawCommand {

    public String name, description;
    public CommandHandler.CommandType type;
}
