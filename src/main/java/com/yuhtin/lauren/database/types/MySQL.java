package com.yuhtin.lauren.database.types;

import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.database.Data;
import lombok.AllArgsConstructor;

import java.sql.*;

@AllArgsConstructor
public class MySQL implements Data {

    final String host;
    final String user;
    final String password;
    final String database;

    @Override
    public Connection openConnection() {
        String url = "jdbc:mysql://" + host + ":3306/" + database + "?autoReconnect=true";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, user, password);
        } catch (Exception exception) {
            exception.printStackTrace();
            Logger.log("Conexão com o MySQL falhou");

            return null;
        }
    }
}