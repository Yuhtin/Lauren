package com.yuhtin.lauren.manager;

import com.google.common.reflect.ClassPath;
import com.google.inject.Injector;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.commands.CommandHandler;
import com.yuhtin.lauren.models.enums.LogType;
import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.service.CommandCache;
import com.yuhtin.lauren.startup.Startup;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.io.IOException;
import java.lang.reflect.Field;

@AllArgsConstructor
public class CommandManager {

    private final JDA bot;
    private final Injector injector;
    private final Logger logger;
    private final String folder;

    public void load() throws IOException {
        CommandCache.start();

        CommandClientBuilder clientBuilder = new CommandClientBuilder();
        clientBuilder.setOwnerId("702518526753243156");
        clientBuilder.setPrefix(Startup.getLauren().getConfig().getPrefix());
        clientBuilder.setAlternativePrefix("%");
        clientBuilder.useHelpBuilder(false);
        clientBuilder.setActivity(Activity.watching("my project on github.com/Yuhtin/Lauren"));

        ClassPath classPath = ClassPath.from(getClass().getClassLoader());

        for (ClassPath.ClassInfo classInfo : classPath.getTopLevelClassesRecursive(folder)) {
            try {

                Class classByName = Class.forName(classInfo.getName());
                Object object = classByName.newInstance();

                if (!(object instanceof Command) || !classByName.isAnnotationPresent(CommandHandler.class)) continue;
                Command command = (Command) object;
                CommandHandler handler = (CommandHandler) classByName.getAnnotation(CommandHandler.class);

                Command rawCommand = new Command(handler.name(),
                        handler.description(),
                        handler.type(),
                        handler.alias());

                Field[] fields = command.getClass().getSuperclass().getDeclaredFields();
                for (Field field : fields) {
                    field.setAccessible(true);

                    if (field.getName().equalsIgnoreCase("name")) field.set(command, handler.name());
                    else if (field.getName().equalsIgnoreCase("aliases")) field.set(command, handler.alias());
                }

                this.injector.injectMembers(command);
                clientBuilder.addCommand(command);

                bot.upsertCommand(new CommandData(rawCommand.getName(), rawCommand.getDescription())).queue();
                CommandCache.insert(handler.type(), rawCommand);

            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException exception) {
                logger.log(LogType.WARNING, "The " + classInfo.getName() + " class could not be instantiated", exception);
            }
        }

        bot.addEventListener(clientBuilder.build());
        CommandCache.construct();

    }

}
