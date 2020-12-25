package com.yuhtin.lauren.sql;

import com.google.inject.Singleton;
import com.yuhtin.lauren.core.statistics.StatsInfo;
import com.yuhtin.lauren.core.xp.Level;
import com.yuhtin.lauren.sql.provider.DatabaseProvider;
import com.yuhtin.lauren.sql.provider.document.parser.impl.ExperienceDocumentParser;
import com.yuhtin.lauren.utils.serialization.Serializer;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@Singleton
public class ExperienceDAO extends DatabaseProvider {

    public void createTable() {

        update("create table if not exists `lauren_levelrewards` ("
                + "`level` int(3) primary key not null, "
                + "`rewards` text"
                + ");"
        );

    }

    public List<Level> findAllLevel() {

        return queryMany("select * from `lauren_levelrewards`")
                .stream()
                .map(document -> document.parse(ExperienceDocumentParser.getInstance()))
                .collect(Collectors.toList());

    }

    public void insertLevel(Level level) {

        update("insert into `lauren_levelrewards` values (?, ?);",
                level.getLevel(),
                Serializer.getLevel().serialize(level)
        );

    }

    public void updateLevel(Level level) {

        update("update `lauren_levelrewards` set `rewards` = ? where `level` = ?",
                Serializer.getLevel().serialize(level),
                level.getLevel()
        );

    }

}
