package com.yuhtin.lauren.core.player.controller;

import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.database.DatabaseController;
import com.yuhtin.lauren.utils.serialization.Serializer;
import io.github.eikefs.sql.provider.query.Query;
import io.github.eikefs.sql.provider.query.TableQuery;
import io.github.eikefs.sql.provider.query.field.TableField;

public class PlayerDatabase {

    public static void createTable() {
        DatabaseController.getDatabase().updateSync(new TableQuery()
                .name("lauren_players", true)
                .fields(new TableField()
                                .name("id")
                                .type("long")
                                .size(18),
                        new TableField()
                                .name("data")
                                .type("text"))
                .primary("id"));
    }

    public static Player loadPlayer(long userID) {
        PlayerORM playerORM = DatabaseController.getDatabase().buildSync(PlayerORM.class, new Query()
                .selectAll()
                .from("lauren_players")
                .where("id", userID)
                .raw());

        if (playerORM == null) {
            create(userID);
            return new Player(userID);
        }

        return Serializer.player.deserialize(playerORM.getData());
    }

    public static void save(long userID, Player player) {
        DatabaseController.getDatabase()
                .updateSync("update `lauren_players` set `data`= '"
                        + Serializer.player.serialize(player) +
                        "' where `id` = '" + userID + "'");
    }

    public static void create(long userID) {
        DatabaseController.getDatabase()
                .updateSync(new Query().insert("lauren_players", userID,
                        Serializer.player.serialize(new Player(userID))));
    }
}
