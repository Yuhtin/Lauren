package com.yuhtin.lauren.models.manager;

import com.google.common.reflect.ClassPath;
import com.yuhtin.lauren.timers.Timer;
import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@Singleton
public class TimerManager {

    @Inject @Named("main") private Logger logger;

    @Getter private final List<Timer> timers = new ArrayList<>();

    public void register(String folder) throws IOException {

        ClassPath classPath = ClassPath.from(getClass().getClassLoader());

        for (ClassPath.ClassInfo classInfo : classPath.getTopLevelClassesRecursive(folder)) {
            try {

                Class classByName = Class.forName(classInfo.getName());
                Object object = classByName.newInstance();
                if (object instanceof Timer) {

                    Timer timer = (Timer) object;
                    timers.add(timer);

                }

            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException exception) {
                this.logger.warning("The " + classInfo.getName() + " class could not be instantiated");
            }
        }

    }

}
