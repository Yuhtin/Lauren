package com.yuhtin.lauren.manager;

import com.google.common.reflect.ClassPath;
import com.google.inject.Injector;
import com.yuhtin.lauren.core.logger.Logger;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.IOException;

@AllArgsConstructor
public class EventsManager {

    private final JDA bot;
    private final Injector injector;
    private final Logger logger;
    private final String folder;

    public void load() throws IOException {

        ClassPath classPath = ClassPath.from(getClass().getClassLoader());

        for (ClassPath.ClassInfo classInfo : classPath.getTopLevelClassesRecursive(folder)) {
            try {
                if (classInfo.getName().contains("BotReadyEvent")) continue;

                Class event = Class.forName(classInfo.getName());
                Object object = event.newInstance();

                injector.injectMembers(object);

                if (object instanceof ListenerAdapter) bot.addEventListener(object);
                else throw new InstantiationException();

            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException exception) {
                logger.warning("The " + classInfo.getName() + " class could not be instantiated");
            }
        }
    }
}
