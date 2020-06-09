package database.types;

import data.controller.PlayerDataController;
import data.PlayerData;
import database.Data;
import lombok.AllArgsConstructor;
import utils.serialization.DataGson;

import java.sql.*;

@AllArgsConstructor
public class MySQL implements Data {
    private Connection connection;
    String host, user, password, database, table;
    int port;

    @Override
    public boolean openConnection() {
        String url = "jdbc:mysql://" + host + ":" + port + "/" + database;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Conexao com o MySQL sucedida!");

            return createTable();
        } catch (Exception e) {
            System.out.println("Conexão com o MySQL falhou");
            return false;
        }
    }

    @Override
    public boolean createTable() {
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS " + table + " (`id` LONG PRIMARY KEY, `data` TEXT);");
            statement.executeUpdate();
            statement.close();

            return true;
        } catch (SQLException e) {
            System.out.println("Não foi possivel criar a tabela");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean loadData() {
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement("SELECT * FROM " + table);

            ResultSet result = statement.executeQuery();
            while (result.next()) {
                PlayerDataController.insert(DataGson.deserialize(result.getString("data")));
            }

            return true;
        } catch (SQLException exception) {
            System.out.println("Não foi possível salvar um dado no banco de dados.");
            return false;
        }
    }

    @Override
    public void save(Long userID, PlayerData controller) {
        PreparedStatement statement;
        try {
            String result = DataGson.serialize(controller);
            statement = connection.prepareStatement("UPDATE " + table + " SET `data` = ? WHERE `id` = ?");
            statement.setString(1, result);
            statement.setLong(2, userID);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
            System.out.println("Não foi possível salvar um dado no banco de dados.");
        }
    }

    @Override
    public void create(Long userID) {
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement("INSERT INTO " + table + "(id, data) VALUES(?,?)");
            statement.setLong(1, userID);
            statement.setString(2, "");
            statement.executeUpdate();
            statement.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
            System.out.println("Não foi possível salvar um dado no banco de dados.");
        }
    }

    @Override
    public void close() {
        if (connection != null) {
            try {
                PlayerDataController.getDATA().forEach(this::save);

                connection.close();
                connection = null;
                System.out.println("Conexão com o banco de dados foi fechada.");
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Não foi possivel fechar a conexão com o banco de dados.");
            }
        }
    }
}
