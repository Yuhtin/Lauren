package com.yuhtin.lauren.core.alarm.controller;

import com.yuhtin.lauren.core.alarm.Alarm;
import com.yuhtin.lauren.database.DatabaseController;
import io.github.eikefs.sql.provider.query.Query;

import java.util.ArrayList;
import java.util.List;

public class AlarmDatabase {

    public static void createTable() {
        DatabaseController.getDatabase().updateSync("create table if not exists `lauren_alarms" +
                "` (`name` varchar(30) primary key not null, `time` text);");
    }

    public static void load() {
        List<Object> query = DatabaseController.getDatabase()
                .querySync(new Query()
                        .selectAll()
                        .from("lauren_alarms"));

        List<Alarm> alarms = new ArrayList<>();
        Alarm tempAlarm = null;
        int i = 1;
        for (Object field : query) {
            if (i == 1) {
                tempAlarm = new Alarm((String) field, null);
                alarms.add(tempAlarm);
                i = 2;
            } else {
                tempAlarm.setTime((String) field);
                i = 1;
            }
        }

        alarms.forEach(AlarmController.get()::registerAlarm);
    }

    public static void save(String name, Alarm alarm) {
        DatabaseController.getDatabase()
                .updateSync("update `lauren_alarms` set `time`= '"
                        + alarm.getTime() +
                        "' where `name` = '" + name + "'");
    }

    public static void create(String name) {
        DatabaseController.getDatabase().querySync(new Query().insert("lauren_alarms", name, ""));
    }
}