package com.yuhtin.lauren.database.types;

import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.database.Data;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

public class SQLite implements Data {

    @Override
    public Connection openConnection() {
        File file = new File("config/lauren.db");
        if (!file.exists()) {

            try {
                file.createNewFile();
            } catch (Exception exception) {
                Logger.error(exception);
            }

        }

        String url = "jdbc:sqlite:" + file;
        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection(url);
        } catch (Exception e) {
            Logger.log("Conexao com o SQLite falhou").save();
            Logger.error(e);

            return null;
        }
    }

}