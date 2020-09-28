package com.yuhtin.lauren.database;

import io.github.eikefs.sql.provider.Provider;
import io.github.eikefs.sql.provider.database.Database;

public class DatabaseController {

    private static final DatabaseController INSTANCE = new DatabaseController();
    public static DatabaseController get() { return INSTANCE; }
    public static Database getDatabase() { return get().database; }

    private Database database;

    /* MySQL Connection */
    public void constructDatabase(String url, String user, String password) {
        database = Provider.getInstance().submit(url, user, password);
    }

    /* SQLite Connection */
    public void constructDatabase(String url) { database = Provider.getInstance().submit(url); }

}
