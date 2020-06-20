package com.yuhtin.lauren.manager;

import com.google.common.reflect.ClassPath;
import com.yuhtin.lauren.core.logger.Logger;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.IOException;

public class EventsManager {
    public EventsManager(JDA bot, String folder) {
        ClassPath cp;

        try {
            cp = ClassPath.from(getClass().getClassLoader());
        } catch (IOException exception) {
            Logger.log("Não foi possível instanciar a ClassPath");
            return;
        }

        for (ClassPath.ClassInfo classInfo : cp.getTopLevelClassesRecursive(folder)) {
            try {
                Class event = Class.forName(classInfo.getName());
                Object object = event.newInstance();

                if (object instanceof ListenerAdapter)
                    bot.addEventListener(object);
                else
                    throw new InstantiationException();

            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException exception) {
                Logger.log("Não foi possível instanciar a classe " + classInfo.getName());
            }
        }
        Logger.log("All events has been registred");
    }
}
