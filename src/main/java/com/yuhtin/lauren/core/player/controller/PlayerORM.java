package com.yuhtin.lauren.core.player.controller;

import io.github.eikefs.sql.provider.orm.ORM;
import io.github.eikefs.sql.provider.orm.annotations.Field;
import io.github.eikefs.sql.provider.orm.annotations.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Table(name = "lauren_players", primary = "id")
public class PlayerORM extends ORM {

    @Field(size = 18, nullable = false)
    private final String id;

    @Field(type = "text", nullable = false)
    private final String data;

    @Field(size = 11, type = "xp", nullable = false)
    private final int xp;

    public static String create() {
        return create(PlayerORM.class);
    }
}
