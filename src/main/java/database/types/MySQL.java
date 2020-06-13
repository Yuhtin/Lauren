package database.types;

import database.Data;
import lombok.AllArgsConstructor;

import java.sql.*;

@AllArgsConstructor
public class MySQL implements Data {
    String host, user, password, database;
    int port;

    @Override
    public Connection openConnection() {
        String url = "jdbc:mysql://" + host + ":" + port + "/" + database;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            System.out.println("Conexão com o MySQL falhou");
            return null;
        }
    }
}