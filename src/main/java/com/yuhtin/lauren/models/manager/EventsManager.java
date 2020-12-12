package com.yuhtin.lauren.models.manager;

import com.google.common.reflect.ClassPath;
import com.yuhtin.lauren.models.enums.LogType;
import com.yuhtin.lauren.core.logger.Logger;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.io.IOException;

public class EventsManager {
    public EventsManager(ShardManager bot, String folder) {
        ClassPath cp;

        try {
            cp = ClassPath.from(getClass().getClassLoader());
        } catch (IOException exception) {
            Logger.log("ClassPath could not be instantiated", LogType.ERROR);
            return;
        }

        for (ClassPath.ClassInfo classInfo : cp.getTopLevelClassesRecursive(folder)) {
            try {
                Class event = Class.forName(classInfo.getName());
                Object object = event.newInstance();

                if (object instanceof ListenerAdapter) bot.addEventListener(object);
                else throw new InstantiationException();
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException exception) {
                Logger.log("The " + classInfo.getName() + " class could not be instantiated", LogType.WARN);
            }
        }
    }
}
