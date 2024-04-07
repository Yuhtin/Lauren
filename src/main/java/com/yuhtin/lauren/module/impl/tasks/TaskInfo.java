package com.yuhtin.lauren.module.impl.tasks;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.TimeUnit;

@Retention(RetentionPolicy.RUNTIME)
public @interface TaskInfo {

    int interval();
    TimeUnit timeType();

}
