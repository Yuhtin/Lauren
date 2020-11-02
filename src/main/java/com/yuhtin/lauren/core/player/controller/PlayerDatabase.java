package com.yuhtin.lauren.core.player.controller;

import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.database.DatabaseController;
import com.yuhtin.lauren.utils.serialization.Serializer;
import io.github.eikefs.sql.provider.query.Query;

public class PlayerDatabase {

    public static void createTable() {
        DatabaseController.getDatabase().updateSync("create table if not exists `lauren_players` (`id` varchar(18) primary key not null, `data` text, `xp` int(11));");
    }

    public static String loadPlayer(long userID) {
        String raw = new Query()
                .selectAll()
                .from("lauren_players")
                .where("id", userID)
                .raw();

        PlayerORM playerORM = DatabaseController.getDatabase().buildSync(PlayerORM.class, raw);
        if (playerORM == null) {
            create(userID);
            return "";
        }

        return playerORM.getData();
    }

    public static void save(long userID, Player player) {
        DatabaseController.getDatabase()
                .updateSync("update `lauren_players`" +
                        " set `data`= '" + Serializer.getPlayer().serialize(player) + "'," +
                        " `xp`='" + player.getExperience() + "' " +
                        "where `id` = '" + userID + "'");
    }

    public static void create(long userID) {
        DatabaseController.getDatabase()
                .updateSync(new Query().insert("lauren_players", userID,
                        Serializer.getPlayer().serialize(new Player(userID)), 0));
    }
}
