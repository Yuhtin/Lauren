package models.cache;

import core.RawCommand;
import models.annotations.CommandHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandCache {

    public static Map<String, RawCommand> commands = new HashMap<>();
    public static Map<CommandHandler.CommandType, List<RawCommand>> commandsType = new HashMap<>();

    public static void start() {
        for (CommandHandler.CommandType value : CommandHandler.CommandType.values()) {
            commandsType.put(value, new ArrayList<>());
        }
    }

    public static void insert(CommandHandler.CommandType type, RawCommand rawCommand) {
        commands.put(rawCommand.name.toLowerCase(), rawCommand);
        commandsType.get(type).add(rawCommand);
    }
}
