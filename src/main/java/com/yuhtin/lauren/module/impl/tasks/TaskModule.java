package com.yuhtin.lauren.module.impl.tasks;

import com.yuhtin.lauren.Lauren;
import com.yuhtin.lauren.module.Module;
import com.yuhtin.lauren.util.LoggerUtil;
import com.yuhtin.lauren.util.PathFinder;
import com.yuhtin.lauren.util.TaskHelper;

import java.util.concurrent.TimeUnit;

public class TaskModule implements Module {

    @Override
    public boolean setup(Lauren lauren) {

        for (Class<?> aClass : PathFinder.from("com.yuhtin.lauren.tasks.impl")) {
            try {
                if (!ExtendedTask.class.isAssignableFrom(aClass)) {
                    lauren.getLogger().warning(aClass.getName() + " is not a Task class!");
                    continue;
                }

                if (!aClass.isAnnotationPresent(TaskInfo.class)) {
                    lauren.getLogger().warning(aClass.getName() + " is not a TaskInfo class!");
                    continue;
                }

                TaskInfo taskInfo = aClass.getAnnotation(TaskInfo.class);
                int period = taskInfo.interval();
                TimeUnit time = taskInfo.timeType();

                ExtendedTask task = (ExtendedTask) aClass.newInstance();

                TaskHelper.runTaskTimerAsync(() -> task.run().queue(), 1, period, time);
            } catch (InstantiationException | IllegalAccessException e) {
                lauren.getLogger().warning("Error while trying to instantiate " + aClass.getName());
                LoggerUtil.printException(e);
            }
        }

        return true;
    }

}
