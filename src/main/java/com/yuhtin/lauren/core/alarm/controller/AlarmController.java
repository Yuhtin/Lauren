package com.yuhtin.lauren.core.alarm.controller;

import com.yuhtin.lauren.core.alarm.Alarm;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class AlarmController {

    public static final AlarmController INSTANCE = new AlarmController();
    public static AlarmController get() { return INSTANCE; }

    @Getter private final Map<String, Alarm> alarms = new HashMap<>();

    public void registerAlarm(Alarm alarm) {
        alarms.put(alarm.getName(), alarm);
    }

}
