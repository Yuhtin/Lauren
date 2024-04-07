package com.yuhtin.lauren.module.impl.timer;

import com.yuhtin.lauren.Lauren;
import com.yuhtin.lauren.module.Module;
import com.yuhtin.lauren.util.PathFinder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TimerModule implements Module {

    private final List<Timer> timers = new ArrayList<>();

    @Override
    public boolean setup(Lauren lauren) {

        PathFinder.from("com.yuhtin.lauren.timer")

        return true;
    }

    public void register(Timer timer) {
        this.timers.add(timer);
    }
}
