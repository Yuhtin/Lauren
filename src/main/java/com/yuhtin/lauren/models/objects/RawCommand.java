package com.yuhtin.lauren.models.objects;

import lombok.AllArgsConstructor;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import lombok.Data;

@AllArgsConstructor
@Data
public class RawCommand {

    private final String name;
    private final String description;
    private final CommandHandler.CommandType type;
    private final String[] aliases;
}
