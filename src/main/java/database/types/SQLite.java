package database.types;

import database.Data;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

public class SQLite implements Data {

    @Override
    public Connection openConnection() {
        File file = new File("config/lauren.db");
        String URL = "jdbc:sqlite:" + file;
        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection(URL);
        } catch (Exception e) {
            System.out.println("Conexao com o SQLite falhou");
            return null;
        }
    }

}