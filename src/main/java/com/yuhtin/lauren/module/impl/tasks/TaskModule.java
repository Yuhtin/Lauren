package com.yuhtin.lauren.module.impl.tasks;

import com.yuhtin.lauren.Lauren;
import com.yuhtin.lauren.module.Module;

public class TaskModule implements Module {

    private Lauren lauren;

    @Override
    public boolean setup(Lauren lauren) {
        this.lauren = lauren;
        return true;
    }
}
