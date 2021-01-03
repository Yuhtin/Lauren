package com.yuhtin.lauren.sql.dao;

import com.google.inject.Singleton;
import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.sql.provider.DatabaseProvider;
import com.yuhtin.lauren.sql.provider.document.parser.impl.PlayerDocumentParser;
import com.yuhtin.lauren.utils.serialization.player.PlayerSerializer;

import javax.annotation.Nullable;

@Singleton
public class PlayerDAO extends DatabaseProvider {

    public void createTable() {
        update("create table if not exists `lauren_players_new` ("
                + "`id` varchar(18) primary key not null, "
                + "`data` text not null, "
                + "`xp` int(11), "
                + "`abbleToDaily` boolean"
                + ");");
    }

    @Nullable
    public Player findById(long userID) {
        return query("select * from `lauren_players_new` where `id` = ?", userID)
                .parse(PlayerDocumentParser.getInstance());
    }

    public void insertPlayer(Player player) {
        update("insert into `lauren_players_new` values (?, ?, ?, ?);",
                player.getUserID(),
                PlayerSerializer.serialize(player),
                player.getExperience(),
                player.isAbbleToDaily()
        );
    }

    public void updatePlayer(Player player) {
        update("update `lauren_players_new` set `data` = ?, `xp` = ?, `abbleToDaily` = ? where `id` = ?",
                PlayerSerializer.serialize(player),
                player.getExperience(),
                player.isAbbleToDaily(),
                player.getUserID()
        );

    }

    public void deletePlayer(long userID) {

        update("delete from `lauren_players_new` where `id` = ?",
                userID
        );

    }

    public void updateAllDailys() {

        update("update `lauren_players_new` set `abbleToDaily` = ?",
                true
        );

    }


}
