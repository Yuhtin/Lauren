package com.yuhtin.lauren.core.player.controller;

import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.database.DatabaseController;
import com.yuhtin.lauren.utils.serialization.Serializer;
import io.github.eikefs.sql.provider.query.Query;

public class PlayerDatabase {

    public static void createTable() {
        DatabaseController.getDatabase().updateSync("create table if not exists `lauren_players` (`id` varchar(18) primary key not null, `data` text);");
    }

    public static Player loadPlayer(long userID) {
        String raw = new Query()
                .selectAll()
                .from("lauren_players")
                .where("id", userID)
                .raw();

        Logger.log(raw);

        PlayerORM playerORM = DatabaseController.getDatabase().buildSync(PlayerORM.class, raw);

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
