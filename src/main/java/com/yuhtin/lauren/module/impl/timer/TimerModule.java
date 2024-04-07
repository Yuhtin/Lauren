package com.yuhtin.lauren.module.impl.timer;

import com.yuhtin.lauren.Lauren;
import com.yuhtin.lauren.module.Module;
import com.yuhtin.lauren.util.LoggerUtil;
import com.yuhtin.lauren.util.PathFinder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TimerModule implements Module {

    private final List<Timer> timers = new ArrayList<>();

    @Override
    public boolean setup(Lauren lauren) {

        for (Class<?> aClass : PathFinder.from("com.yuhtin.lauren.timer.impl")) {
            try {
                if (!Timer.class.isAssignableFrom(aClass)) {
                    lauren.getLogger().warning(aClass.getName() + " is not a Timer class!");
                    continue;
                }

                register((Timer) aClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                lauren.getLogger().warning("Error while trying to instantiate " + aClass.getName());
                LoggerUtil.printException(e);
            }
        }

        return true;
    }

    public void register(Timer timer) {
        this.timers.add(timer);
    }
}
