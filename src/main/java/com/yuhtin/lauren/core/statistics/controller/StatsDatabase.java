package com.yuhtin.lauren.core.statistics.controller;

import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.statistics.StatsInfo;
import com.yuhtin.lauren.database.DatabaseController;
import com.yuhtin.lauren.utils.serialization.Serializer;
import io.github.eikefs.sql.provider.query.Query;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StatsDatabase {

    public static void createTable() {
        DatabaseController.getDatabase().updateSync("create table if not exists `lauren_stats` (`tipo` varchar(40) primary key not null, `data` longtext);");
    }

    public static void load() {
        try (PreparedStatement statement = DatabaseController.get().getConnection().prepareStatement("select * from `lauren_stats`")) {

            ResultSet query = statement.executeQuery();
            while (query.next()) {
                StatsController.get().getStats().put(query.getString("tipo"),
                        Serializer.getStats().deserialize(query.getString("data")));
            }

            query.close();
        } catch (
                SQLException exception) {
            Logger.log("Could not load stats from database");
            exception.printStackTrace();
        }

    }

    public static void save() {
        Logger.log("Saving all stats, the bot may lag for a bit");

        for (String name : StatsController.get().getStats().keySet()) {
            DatabaseController.getDatabase()
                    .updateSync("update `lauren_stats` set `data`= '"
                            + Serializer.getStats().serialize(StatsController.get().getStats().get(name)) +
                            "' where `tipo` = '" + name + "'");
        }

        Logger.log("Saved all stats");
    }

    public static void create(String name) {
        DatabaseController.getDatabase()
                .updateSync(new Query().insert("lauren_stats", name,
                        Serializer.getStats().serialize(new StatsInfo(name))));
    }
}
