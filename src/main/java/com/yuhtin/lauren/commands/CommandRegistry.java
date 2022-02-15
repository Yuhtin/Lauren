package com.yuhtin.lauren.commands;

import com.google.common.reflect.ClassPath;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.models.enums.LogType;
import com.yuhtin.lauren.startup.Startup;
import lombok.val;
import lombok.var;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.Command.Subcommand;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.io.IOException;
import java.util.HashMap;
import java.util.stream.Collectors;

@lombok.Data(staticConstructor = "of")
public class CommandRegistry {

    private final JDA client;
    private final Injector injector;

    @Inject private Logger logger;

    public void register() {
        ClassPath classPath;
        try {
            classPath = ClassPath.from(getClass().getClassLoader());
        } catch (IOException exception) {
            logger.severe("ClassPath could not be instantiated");
            return;
        }

        val infoCacher = InfoCacher.getInstance();
        infoCacher.start();

        val commands = new HashMap<String, CommandData>();
        val commandMap = Startup.getLauren().getCommandCatcher().getCommandMap();
        for (val info : classPath.getTopLevelClassesRecursive("com.yuhtin.lauren.commands.impl")) {
            try {
                val name = Class.forName(info.getName());
                val object = name.newInstance();

                if (name.isAnnotationPresent(CommandInfo.class)) {
                    val command = (Command) object;
                    injector.injectMembers(command);

                    val data = (CommandInfo) name.getAnnotation(CommandInfo.class);

                    var commandName = data.name();
                    commandMap.register(commandName, command);

                    var subCommand = "";
                    if (commandName.contains(".")) {
                        val split = commandName.split("\\.");
                        commandName = split[0];
                        subCommand = split[1];
                    }

                    val commandData = commands.getOrDefault(commandName, new CommandData(commandName, data.description()));
                    if (!subCommand.equalsIgnoreCase("")) {
                        val subcommandData = new SubcommandData(subCommand, data.description());
                        commandData.addSubcommands(subcommandData);

                        argsInterpreter(data, null, subcommandData);
                    } else {
                        argsInterpreter(data, commandData, null);
                    }

                    if (commands.containsKey(commandName)) commands.replace(commandName, commandData);
                    else commands.put(commandName, commandData);

                    infoCacher.insert(data);
                } else throw new InstantiationException();
            } catch (Exception exception) {
                logger.log(LogType.SEVERE, "The " + info.getName() + " class could not be instantiated", exception);
            }

        }

        infoCacher.construct();

        client.retrieveCommands().queue(createdCommands -> {
            for (val command : commands.values()) {
                boolean exists = false;
                for (val createdCommand : createdCommands) {
                    if (createdCommand.getName().equals(command.getName())) {
                        exists = true;

                        val createdSubcommands = createdCommand.getSubcommands().stream().map(Subcommand::getName).collect(Collectors.toList());
                        for (val commandSubcommand : command.getSubcommands()) {
                            if (!createdSubcommands.contains(commandSubcommand.getName())) {
                                exists = false;
                            }
                        }

                        if (!createdCommand.getDescription().equals(command.getDescription())) {
                            exists = false;
                        }
                    }
                }

                if (!exists) {
                    logger.info("Adding " + command.getName() + " because is a new command.");
                    client.upsertCommand(command).queue();
                }
            }
        });

        logger.info("Registered " + commandMap.getCommands().size() + " commands successfully");
    }

    private void argsInterpreter(CommandInfo handler, CommandData commandData, SubcommandData subcommandData) {
        for (val option : handler.args()) {
            val split = option.split("-");
            val argName = split[0];
            OptionType optionType;

            if (argName.contains("@")) optionType = OptionType.USER;
            else if (argName.contains("%")) optionType = OptionType.ROLE;
            else if (argName.contains("#")) optionType = OptionType.CHANNEL;
            else if (argName.contains("!")) optionType = OptionType.INTEGER;
            else optionType = OptionType.STRING;

            val reformatedName = argName.replace("[", "")
                    .replace("]", "")
                    .replace("!", "")
                    .replace("<", "")
                    .replace(">", "")
                    .replace("@", "")
                    .replace("%", "")
                    .replace("#", "");

            val optionData = new OptionData(
                    optionType,
                    reformatedName,
                    split[1],
                    argName.contains("<")
            );

            if (commandData != null) commandData.addOptions(optionData);
            if (subcommandData != null) subcommandData.addOptions(optionData);
        }
    }

}
