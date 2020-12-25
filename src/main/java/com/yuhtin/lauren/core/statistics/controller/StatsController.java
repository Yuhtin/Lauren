package com.yuhtin.lauren.core.statistics.controller;

import com.yuhtin.lauren.core.statistics.StatsInfo;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class StatsController {

    private static final StatsController INSTANCE = new StatsController();
    @Getter private final Map<String, StatsInfo> stats = new HashMap<>();

    public StatsInfo getStats(String name) {

        StatsInfo info = stats.getOrDefault(name, null);
        if (info == null) {

            info = new StatsInfo(name);
            stats.put(name, info);

            StatsDatabase.create(name);

        }

        return info;

    }

    public static StatsController get() { return INSTANCE; }
}
