package com.yuhtin.lauren.connectiontest;

import com.yuhtin.lauren.core.player.controller.PlayerORM;
import io.github.eikefs.sql.provider.Provider;
import io.github.eikefs.sql.provider.database.Database;
import io.github.eikefs.sql.provider.query.Query;
import io.github.eikefs.sql.provider.query.TableQuery;
import io.github.eikefs.sql.provider.query.field.TableField;

import java.io.File;
import java.util.List;

public class SQLTest {

    public static void main(String[] args) {
        Database database = Provider.getInstance().submit(new File("config/lauren.db").toString());
        long userID = 272879983326658570L;


        // Creating tables
        database.updateSync(new TableQuery()
                .name("users_test")
                .fields(new TableField()
                                .name("id")
                                .type("long")
                                .size(18),
                        new TableField()
                                .name("data")
                                .type("text"))
                .primary("id"));

        // Inserting data
        database.updateSync(new Query().insert("users_test", userID, "Yuhtin"));
        database.updateSync(new Query().insert("users_test", 704680244774305802L, "eu venero o adulto ney"));

        // Getting the data
        PlayerORM player = database.buildSync(PlayerORM.class, new Query()
                .selectAll()
                .from("users_test")
                .where("id", userID)
                .raw());

        database.updateSync("update `users_test` set `data`= 'Omnitrix Flamejante' where `id` = '" + userID + "'");

        String name = database.querySync(new Query()
                .selectAll()
                .from("users_test")
                .where("id", userID)
                .raw())
                .get(1)
                .toString();

        System.out.println("ID: " + player.getId());
        System.out.println("Name: " + player.getData());
        System.out.println(name);

        System.out.println(new Query()
                .selectAll()
                .from("users_test")
                .raw());

        List<Object> users_test = database.querySync(new Query().selectAll().from("users_test"));
        for (Object o : users_test) {
            System.out.println(o);
        }

        database.updateSync(new Query().drop("users_test", "table"));

        database.shutdown();
    }
}
