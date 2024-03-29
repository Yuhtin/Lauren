package com.yuhtin.lauren.core.statistics;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.yuhtin.lauren.sql.dao.StatisticDAO;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class StatsController {

    @Getter @Inject private StatisticDAO statisticDAO;

    @Getter private final Map<String, StatsInfo> stats = new HashMap<>();

    public StatsInfo getStats(String name) {
        StatsInfo info = stats.getOrDefault(name, null);
        if (info == null) {
            info = new StatsInfo(name);
            stats.put(name, info);

            statisticDAO.insertStatistic(info);
        }

        return info;
    }

    public void insertStats(StatsInfo statsInfo) {
        this.stats.put(statsInfo.getName(), statsInfo);
    }

}
