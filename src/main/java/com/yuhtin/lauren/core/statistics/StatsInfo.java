package com.yuhtin.lauren.core.statistics;

import lombok.Data;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
public class StatsInfo {

    private final String name;
    private final Map<String, Integer> monthStats = new HashMap<>();
    private int totalStats;

    public void suplyStats(int number) {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int month = calendar.get(Calendar.MONTH),
                year = calendar.get(Calendar.YEAR);

        String field = month + "/" + year;
        if (monthStats.containsKey(field)) monthStats.replace(field, monthStats.get(field) + number);
        else monthStats.put(field, number);

        totalStats += number;
    }

    public int getStats(String name) { return monthStats.getOrDefault(name, 0); }

}
