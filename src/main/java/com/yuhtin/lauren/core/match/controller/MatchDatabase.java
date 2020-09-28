package com.yuhtin.lauren.core.match.controller;

import com.yuhtin.lauren.core.match.Game;
import com.yuhtin.lauren.core.match.Match;
import com.yuhtin.lauren.database.DatabaseController;
import com.yuhtin.lauren.models.enums.GameMode;
import com.yuhtin.lauren.models.enums.GameType;
import com.yuhtin.lauren.utils.serialization.Serializer;
import io.github.eikefs.sql.provider.query.Query;

import java.util.ArrayList;
import java.util.List;

public class MatchDatabase {

    public static void createTable() {
        DatabaseController.getDatabase().updateSync("create table if not exists `lauren_matches` (`id` varchar(15) primary key not null, `data` text);");
    }

    // pog nivel 100000000000000000000000000
    public static void loadData() {
        List<Object> query = DatabaseController.getDatabase()
                .querySync(new Query()
                        .selectAll()
                        .from("lauren_matches"));

        List<MatchORM> matches = new ArrayList<>();
        MatchORM matchORM = null;
        int i = 1;
        for (Object field : query) {
            if (i == 1) {
                matchORM = new MatchORM((String) field, null);
                matches.add(matchORM);
                i = 2;
            } else {
                matchORM.setData((String) field);
                i = 1;
            }
        }

        for (MatchORM match : matches) {
            MatchController.insert(Serializer.match.deserialize(match.getData()));
        }

    }

    public static void save(String id, Match match) {
        DatabaseController.getDatabase()
                .updateSync("update `lauren_mathces` set `data`= '"
                        + Serializer.match.serialize(match) +
                        "' where `id` = '" + id + "'");
    }

    public static void create(String id) {
        DatabaseController.getDatabase()
                .querySync(new Query().insert("lauren_matches", id, new Match(new Game(GameType.LUDO, GameMode.CASUAL))));
    }
}
