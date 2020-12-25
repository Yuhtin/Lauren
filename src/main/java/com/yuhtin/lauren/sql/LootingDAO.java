package com.yuhtin.lauren.sql;

import com.google.inject.Singleton;
import com.yuhtin.minecraft.steellooting.api.player.LootingPlayer;
import com.yuhtin.minecraft.steellooting.sql.provider.DatabaseProvider;
import com.yuhtin.minecraft.steellooting.sql.provider.document.parser.impl.LootingPlayerDocumentParser;

import javax.annotation.Nullable;

@Singleton
public class LootingDAO extends DatabaseProvider {

    public void createTable() {
        update("create table if not exists `steel_looting` ("
                + "`name` char(16) not null, "
                + "`lootingLimit` double not null"
                + ");");
    }

    @Nullable
    public LootingPlayer findByName(String name) {
        return query("select * from `steel_looting` where `name` = ?", name)
                .parse(LootingPlayerDocumentParser.getInstance());
    }

    public void insertPlayer(String name, LootingPlayer lootingPlayer) {
        update("insert into `steel_looting` values (?, ?);",
                name,
                lootingPlayer.getLootingLimit()
        );
    }

    public void updatePlayer(String name, LootingPlayer lootingPlayer) {
        update("update `steel_looting` set `lootingLimit` = ? where `name` = ?",
                lootingPlayer.getLootingLimit(),
                name
        );

    }

}
