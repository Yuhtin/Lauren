package models.cache;

import core.RawCommand;
import models.annotations.CommandHandler;

import java.util.HashMap;
import java.util.Map;

public class CommandCache {

    public static Map<CommandHandler.CommandType, RawCommand> commands = new HashMap<>();
}
