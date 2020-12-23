package com.yuhtin.lauren.models.manager;

import com.google.common.reflect.ClassPath;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.models.enums.LogType;
import com.yuhtin.lauren.timers.Timer;
import lombok.Getter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

public class TimerManager {

    @Getter private static TimerManager instance;

    @Getter private List<Timer> timers = new ArrayList<>();
    private String folder;

    public TimerManager(String folder) {
        this.folder = folder;
        instance = this;
    }

    public void register() {

        ClassPath classPath;
        try {

            classPath = ClassPath.from(getClass().getClassLoader());

        } catch (IOException exception) {

            Logger.log("ClassPath could not be instantiated", LogType.ERROR);
            return;

        }

        for (ClassPath.ClassInfo classInfo : classPath.getTopLevelClassesRecursive(folder)) {
            try {

                Class classByName = Class.forName(classInfo.getName());
                Object object = classByName.newInstance();
                if (object instanceof Timer) {

                    Timer timer = (Timer) object;
                    timers.add(timer);

                }

            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException exception) {
                Logger.log("The " + classInfo.getName() + " class could not be instantiated", LogType.WARN);
            }
        }

    }

}
