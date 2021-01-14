package com.yuhtin.lauren.sql.dao;

import com.google.inject.Singleton;
import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.core.statistics.StatsInfo;
import com.yuhtin.lauren.sql.provider.DatabaseProvider;
import com.yuhtin.lauren.sql.provider.document.parser.impl.StatisticDocumentParser;
import com.yuhtin.lauren.utils.serialization.Serializer;
import com.yuhtin.lauren.utils.serialization.player.PlayerSerializer;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@Singleton
public class StatisticDAO extends DatabaseProvider {

    public void createTable() {

        update("create table if not exists `lauren_stats` ("
                + "`type` varchar(40) primary key not null, "
                + "`data` longtext"
                + " );"
        );

    }

    public List<StatsInfo> findAllStats() {

        return queryMany("select * from `lauren_stats`")
                .stream()
                .map(document -> document.parse(StatisticDocumentParser.getInstance()))
                .collect(Collectors.toList());

    }


    public void insertStatistic(StatsInfo info) {

        update("insert into `lauren_stats` values (?, ?);",
                info.getName(),
                Serializer.getStats().serialize(info)
        );

    }

    public void updateStatistic(StatsInfo info) {

        update("update `lauren_stats` set `data` = ? where `type` = ?",
                Serializer.getStats().serialize(info),
                info.getName()
        );

    }

}
