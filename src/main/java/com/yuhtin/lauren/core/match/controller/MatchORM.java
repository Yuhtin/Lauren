package com.yuhtin.lauren.core.match.controller;

import io.github.eikefs.sql.provider.orm.ORM;
import io.github.eikefs.sql.provider.orm.annotations.Field;
import io.github.eikefs.sql.provider.orm.annotations.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Table(name = "lauren_matches", primary = "id")
public class MatchORM extends ORM {

    @Field(size = 15, nullable = false)
    private final String id;

    @Field(type = "text", nullable = false)
    private String data;

    public static String create() {
        return create(MatchORM.class);
    }
}
