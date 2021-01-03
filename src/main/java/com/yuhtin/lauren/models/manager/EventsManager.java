package com.yuhtin.lauren.models.manager;

import com.google.common.reflect.ClassPath;
import com.google.inject.Injector;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.io.IOException;
import java.util.logging.Logger;

@AllArgsConstructor
public class EventsManager {

    private final ShardManager bot;
    private final Injector injector;
    private final Logger logger;
    private final String folder;

    public void load() throws IOException {

        ClassPath classPath = ClassPath.from(getClass().getClassLoader());

        for (ClassPath.ClassInfo classInfo : classPath.getTopLevelClassesRecursive(folder)) {
            try {
                Class event = Class.forName(classInfo.getName());
                Object object = event.newInstance();

                this.injector.injectMembers(object);

                if (object instanceof ListenerAdapter) bot.addEventListener(object);
                else throw new InstantiationException();

            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException exception) {
                this.logger.warning("The " + classInfo.getName() + " class could not be instantiated");
            }
        }
    }
}
