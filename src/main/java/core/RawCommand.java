package core;

import lombok.AllArgsConstructor;
import models.annotations.CommandHandler;

@AllArgsConstructor
public class RawCommand {

    public String name, description;
    public CommandHandler.CommandType type;
}
