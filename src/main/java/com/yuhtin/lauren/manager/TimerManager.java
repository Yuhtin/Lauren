package com.yuhtin.lauren.manager;

import com.google.common.reflect.ClassPath;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.startup.Startup;
import com.yuhtin.lauren.timers.Timer;
import lombok.Getter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@Singleton
public class TimerManager {

    @Inject private Logger logger;

    @Getter private final List<Timer> timers = new ArrayList<>();

    public void register(String folder) throws IOException {

        ClassPath classPath = ClassPath.from(getClass().getClassLoader());

        for (ClassPath.ClassInfo classInfo : classPath.getTopLevelClassesRecursive(folder)) {
            try {

                Class classByName = Class.forName(classInfo.getName());
                Object object = classByName.newInstance();
                if (object instanceof Timer) {

                    Timer timer = (Timer) object;
                    Startup.getLauren().getInjector().injectMembers(timer);

                    timers.add(timer);

                }

            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException exception) {
                this.logger.warning("The " + classInfo.getName() + " class could not be instantiated");
            }
        }

    }

}
