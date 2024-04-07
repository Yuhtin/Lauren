package com.yuhtin.lauren.module.impl.timer;

import java.util.List;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public interface Timer {

    String name();

    /**
     * @return day that the timer will run (eg: MONDAY, TUESDAY, WEDNESDAY)
     */
    String day();

    /**
     * @return a list of hours that the timer will run (eg: 13:00, 14:00, 15:30)
     */
    List<String> hours();
    void run();

}
