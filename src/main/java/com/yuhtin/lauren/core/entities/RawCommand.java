package com.yuhtin.lauren.core.entities;

import lombok.AllArgsConstructor;
import com.yuhtin.lauren.models.annotations.CommandHandler;

import java.util.List;

@AllArgsConstructor
public class RawCommand {

    public String name, description;
    public CommandHandler.CommandType type;
    public String[] aliases;
}
