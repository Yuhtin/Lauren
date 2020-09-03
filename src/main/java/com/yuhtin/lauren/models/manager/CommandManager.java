package com.yuhtin.lauren.models.manager;

import com.google.common.reflect.ClassPath;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.yuhtin.lauren.Lauren;
import com.yuhtin.lauren.core.entities.RawCommand;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import com.yuhtin.lauren.models.enums.LogType;
import com.yuhtin.lauren.service.CommandCache;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;

import java.io.IOException;
import java.lang.reflect.Field;

public class CommandManager {
    public CommandManager(JDA bot, String folder) {
        CommandCache.start();

        CommandClientBuilder clientBuilder = new CommandClientBuilder();
        clientBuilder.setOwnerId("702518526753243156");
        clientBuilder.setPrefix(Lauren.config.prefix);
        clientBuilder.setHelpWord("riphelpmessage");
        clientBuilder.setActivity(Activity.watching("my project on github.com/Yuhtin/Lauren"));
        ClassPath cp;

        try {
            cp = ClassPath.from(getClass().getClassLoader());
        } catch (IOException exception) {
            Logger.log("ClassPath could not be instantiated", LogType.ERROR);
            return;
        }

        for (ClassPath.ClassInfo classInfo : cp.getTopLevelClassesRecursive(folder)) {
            try {
                Class aClass = Class.forName(classInfo.getName());
                Object object = aClass.newInstance();

                if (object instanceof Command && aClass.isAnnotationPresent(CommandHandler.class)) {
                    Command command = (Command) object;
                    CommandHandler handler = (CommandHandler) aClass.getAnnotation(CommandHandler.class);
                    CommandCache.insert(handler.type(), new RawCommand(handler.name(), handler.description(), handler.type(), handler.alias()));

                    Field[] fields = command.getClass().getSuperclass().getDeclaredFields();
                    for (Field field : fields) {
                        field.setAccessible(true);

                        if (field.getName().equalsIgnoreCase("name")) field.set(command, handler.name());
                        else if (field.getName().equalsIgnoreCase("aliases")) field.set(command, handler.alias());
                    }

                    clientBuilder.addCommand(command);
                } else
                    throw new InstantiationException();

            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException exception) {
                Logger.log("The " + classInfo.getName() + " class could not be instantiated", LogType.WARN);
            }
        }
        bot.addEventListener(clientBuilder.build());

        CommandCache.construct();
    }

}
