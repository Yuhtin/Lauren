package com.yuhtin.lauren.sql.connection.sqlite;

import com.yuhtin.lauren.sql.connection.ConnectionInfo;
import com.yuhtin.lauren.sql.connection.SQLConnection;
import lombok.SneakyThrows;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

@SuppressWarnings("ResultOfMethodCallIgnored")
public final class SQLiteConnection implements SQLConnection {

    private File sqlFile;
    private Connection connection;

    @SneakyThrows
    @Override
    public boolean configure(ConnectionInfo info) {

        this.sqlFile = createSQLFile(info.getFile());
        return true;

    }

    @Override
    public Connection findConnection() {
        if (connection == null) {

            try {

                Class.forName("org.sqlite.JDBC");
                this.connection = DriverManager.getConnection("jdbc:sqlite:" + this.sqlFile);

            } catch (Throwable throwable) {

                throwable.printStackTrace();

            }

        }

        return this.connection;
    }

    @SneakyThrows
    private File createSQLFile(String name) {
        File file = new File(name);

        if (!file.exists()) {

            File parentFile = file.getParentFile();
            parentFile.mkdirs();

            file.createNewFile();
        }

        return file;
    }

}
