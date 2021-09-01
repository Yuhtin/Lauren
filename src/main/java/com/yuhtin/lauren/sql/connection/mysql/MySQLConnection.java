package com.yuhtin.lauren.sql.connection.mysql;

import com.yuhtin.lauren.sql.connection.ConnectionInfo;
import com.yuhtin.lauren.sql.connection.SQLConnection;

import java.sql.Connection;
import java.sql.DriverManager;

public final class MySQLConnection implements SQLConnection {

    private Connection connection;

    @Override
    public boolean configure(ConnectionInfo info) {

        String url = "jdbc:mysql://" + info.getHost() + ":3306/" + info.getDatabase() + "?autoReconnect=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, info.getUsername(), info.getPassword());
            return true;

        } catch (Exception exception) {

            exception.printStackTrace();
            return false;

        }

    }

    @Override
    public Connection findConnection() {

        return connection;

    }

}
