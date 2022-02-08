package com.yuhtin.lauren.commands;

import com.google.common.reflect.ClassPath;
import com.google.inject.Inject;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.startup.Startup;
import lombok.val;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.io.IOException;
import java.util.ArrayList;

@lombok.Data(staticConstructor = "of")
public class CommandRegistry {

    @Inject private Logger logger;
    private final JDA client;

    public void register() {

        ClassPath classPath;
        try {
            classPath = ClassPath.from(getClass().getClassLoader());
        } catch (IOException exception) {
            logger.severe("ClassPath could not be instantiated");
            return;
        }

        val infoCacher = InfoCacher.getInstance();
        val commands = new ArrayList<net.dv8tion.jda.api.interactions.commands.build.CommandData>();
        val commandMap = Startup.getLauren().getCommandCatcher().getCommandMap();
        for (val info : classPath.getTopLevelClassesRecursive("com.yuhtin.lauren.commands.impl")) {
            try {
                val name = Class.forName(info.getName());
                val object = name.newInstance();

                if (name.isAnnotationPresent(CommandData.class)) {
                    val command = (Command) object;
                    val data = (CommandData) name.getAnnotation(CommandData.class);

                    commandMap.register(data.name(), command);

                    val commandData = new net.dv8tion.jda.api.interactions.commands.build.CommandData(data.name(), data.description());
                    argsInterpreter(data, commandData);

                    commands.add(commandData);
                    infoCacher.insert(data);
                } else throw new InstantiationException();
            } catch (Exception exception) {
                exception.printStackTrace();
                logger.severe("The " + info.getName() + " class could not be instantiated");
            }

            infoCacher.construct();
        }

        client.retrieveCommands().queue(createdCommands -> {
            for (val command : commands) {
                boolean exists = false;
                for (val createdCommand : createdCommands) {
                    if (createdCommand.getName().equals(command.getName())) {
                        exists = true;
                        break;
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

    private void argsInterpreter(CommandData handler, net.dv8tion.jda.api.interactions.commands.build.CommandData commandData) {
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

            commandData.addOptions(optionData);
        }
    }

}
