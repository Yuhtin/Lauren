package com.yuhtin.lauren.database;

import com.yuhtin.lauren.core.alarm.controller.AlarmDatabase;
import com.yuhtin.lauren.core.player.controller.PlayerDatabase;
import com.yuhtin.lauren.core.statistics.controller.StatsDatabase;
import io.github.eikefs.sql.provider.Provider;
import io.github.eikefs.sql.provider.database.Database;
import lombok.Getter;

import java.sql.Connection;

public class DatabaseController {

    @Getter private Connection connection;
    private static final DatabaseController INSTANCE = new DatabaseController();
    public static DatabaseController get() { return INSTANCE; }
    public static Database getDatabase() { return get().database; }

    private Database database;

    /* MySQL Connection */
    public void constructDatabase(String url, String user, String password) {
        database = Provider.getInstance().submit(url, user, password);
    }

    /* SQLite Connection */
    public void constructDatabase(Connection connection) {
        this.connection = connection;
        this.database = new Database(connection);
    }

    public void loadAll() {
        PlayerDatabase.createTable();
        AlarmDatabase.createTable();
        AlarmDatabase.load();
        StatsDatabase.createTable();
        StatsDatabase.load();
    }
}
