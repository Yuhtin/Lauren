package manager;

import application.Lauren;
import com.google.common.reflect.ClassPath;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import core.RawCommand;
import logger.Logger;
import models.annotations.CommandHandler;
import models.cache.CommandCache;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;

import java.io.IOException;

public class CommandStartup {
    public CommandStartup(JDA bot, String folder) {

        CommandClientBuilder clientBuilder = new CommandClientBuilder();
        clientBuilder.setOwnerId("702518526753243156");
        clientBuilder.setPrefix(Lauren.config.prefix);
        clientBuilder.setHelpWord("riphelpmessage");
        clientBuilder.setActivity(Activity.watching("my project on github.com/Yuhtin/Lauren"));
        ClassPath cp;

        try {
            cp = ClassPath.from(getClass().getClassLoader());
        } catch (IOException exception) {
            Logger.log("ClassPath could not be instantiated");
            return;
        }

        for (ClassPath.ClassInfo classInfo : cp.getTopLevelClassesRecursive(folder)) {
            try {
                Class aClass = Class.forName(classInfo.getName());
                Object object = aClass.newInstance();

                if (object instanceof Command) {
                    if (aClass.isAnnotationPresent(CommandHandler.class)) {
                        CommandHandler type = this.getClass().getAnnotation(CommandHandler.class);
                        CommandCache.commands.put(type.type(), new RawCommand(type.name(), type.description()));
                    }

                    clientBuilder.addCommand((Command) object);
                } else
                    throw new InstantiationException();

            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException exception) {
                Logger.log("The " + classInfo.getName() + " class could not be instantiated");
            }
        }

        bot.addEventListener(clientBuilder.build());
        Logger.log("All commands has been registred").save();
    }

}
