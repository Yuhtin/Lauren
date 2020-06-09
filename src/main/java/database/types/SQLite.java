package database.types;

import dao.PlayerData;
import dao.controller.PlayerDataController;
import database.Data;
import lombok.AllArgsConstructor;
import utils.serialization.DataGson;

import java.io.File;
import java.sql.*;

@AllArgsConstructor
public class SQLite implements Data {
    private Connection connection;
    private final String table;

    @Override
    public boolean openConnection() {
        File file = new File("config/lauren_players.db");
        String URL = "jdbc:sqlite:" + file;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(URL);
            System.out.println("Conexao com o SQLite sucedida");

            return createTable();
        } catch (Exception e) {
            System.out.println("Conexao com o SQLite falhou");
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
                PlayerData.insert(DataGson.deserialize(result.getString("data")));
            }

            return true;
        } catch (SQLException exception) {
            System.out.println("Não foi possível salvar um dado no banco de dados.");
            return false;
        }
    }

    @Override
    public void save(Long userID, PlayerDataController controller) {
        PreparedStatement statement;
        try {
            String result = DataGson.serialize(controller);
            statement = connection.prepareStatement("INSERT INTO " + table + " (id, data) VALUES(" + userID + "," + result + ") ON DUPLICATE KEY UPDATE tag=tag, id=id, data='" + result + "'");
            statement.executeUpdate();
            statement.close();
        } catch (SQLException exception) {
            System.out.println("Não foi possível salvar um dado no banco de dados.");
        }
    }

    @Override
    public void close() {
        if (connection != null) {
            try {
                PlayerData.getDATA().forEach(this::save);

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