package manager;

import application.Lauren;
import com.google.common.reflect.ClassPath;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import logger.Logger;
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
                Class command = Class.forName(classInfo.getName());
                Object object = command.newInstance();

                if (object instanceof Command)
                    clientBuilder.addCommand((Command) object);
                else
                    throw new InstantiationException();

            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException exception) {
                Logger.log("The " + classInfo.getName() + " class could not be instantiated");
            }
        }

        bot.addEventListener(clientBuilder.build());
        Logger.log("All commands has been registred").save();
    }

}
