package com.yuhtin.lauren.models.manager;

import com.google.common.reflect.ClassPath;
import com.google.inject.Injector;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.yuhtin.lauren.LaurenStartup;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import com.yuhtin.lauren.models.objects.RawCommand;
import com.yuhtin.lauren.service.CommandCache;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.logging.Logger;

@AllArgsConstructor
public class CommandManager {

    private final ShardManager bot;
    private final Injector injector;
    private final Logger logger;
    private final String folder;

    public void load() throws IOException {
        CommandCache.start();

        CommandClientBuilder clientBuilder = new CommandClientBuilder();
        clientBuilder.setOwnerId("702518526753243156");
        clientBuilder.setPrefix(LaurenStartup.getInstance().getConfig().getPrefix());
        clientBuilder.useHelpBuilder(false);
        clientBuilder.setActivity(Activity.watching("my project on github.com/Yuhtin/Lauren"));

        ClassPath classPath = ClassPath.from(getClass().getClassLoader());

        for (ClassPath.ClassInfo classInfo : classPath.getTopLevelClassesRecursive(folder)) {
            try {

                Class classByName = Class.forName(classInfo.getName());
                Object object = classByName.newInstance();

                if (object instanceof Command && classByName.isAnnotationPresent(CommandHandler.class)) {
                    Command command = (Command) object;
                    CommandHandler handler = (CommandHandler) classByName.getAnnotation(CommandHandler.class);

                    RawCommand rawCommand = new RawCommand(handler.name(),
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
                    CommandCache.insert(handler.type(), rawCommand);

                } else throw new InstantiationException();

            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException exception) {
                logger.warning("The " + classInfo.getName() + " class could not be instantiated");
            }
        }

        bot.addEventListener(clientBuilder.build());
        CommandCache.construct();

    }

}
