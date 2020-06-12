package database;

import data.PlayerData;
import data.controller.PlayerDataController;
import matches.Match;
import matches.controller.MatchController;
import utils.serialization.MatchGson;
import utils.serialization.PlayerDataGson;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {

    public final Connection connection;
    public final String tableMatches, tablePlayers;

    public Database(Data data, String table) {
        connection = data.openConnection();

        this.tableMatches = table + "_matches";
        this.tablePlayers = table + "_players";
    }

    public boolean createTable() {
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS " + tablePlayers + " (`id` LONG PRIMARY KEY, `data` TEXT);");
            statement.executeUpdate();
            statement.close();

            statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + tableMatches + " (`id` VARCHAR(15), `data` TEXT);");
            statement.executeUpdate();
            statement.close();
            return true;
        } catch (SQLException e) {
            System.out.println("Não foi possivel criar a tabela");
            e.printStackTrace();
            return false;
        }
    }

    public boolean loadData() {
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement("SELECT * FROM " + tablePlayers);

            ResultSet result = statement.executeQuery();
            statement.close();
            while (result.next()) {
                PlayerDataController.insert(PlayerDataGson.deserialize(result.getString("data")));
            }
            statement.close();

            statement = connection.prepareStatement("SELECT * FROM " + tableMatches);
            result = statement.executeQuery();
            while (result.next()) {
                MatchController.insert(MatchGson.deserialize(result.getString("data")));
            }
            statement.close();

            return true;
        } catch (SQLException exception) {
            exception.printStackTrace();
            System.out.println("Não foi possível salvar um dado no banco de dados.");
            return false;
        }
    }

    public void save(Long userID, PlayerData controller) {
        PreparedStatement statement;
        try {
            String result = PlayerDataGson.serialize(controller);
            statement = connection.prepareStatement("UPDATE " + tablePlayers + " SET `data` = ? WHERE `id` = ?");
            statement.setString(1, result);
            statement.setLong(2, userID);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
            System.out.println("Não foi possível salvar um dado no banco de dados.");
        }
    }

    public void save(String id, Match match) {
        PreparedStatement statement;
        try {
            String result = MatchGson.serialize(match);
            statement = connection.prepareStatement("UPDATE " + tableMatches + " SET `data` = ? WHERE `id` = ?");
            statement.setString(1, result);
            statement.setString(2, id);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
            System.out.println("Não foi possível salvar um dado no banco de dados.");
        }
    }

    public void create(Long userID) {
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement("INSERT INTO " + tablePlayers + "(id, data) VALUES(?,?)");
            statement.setLong(1, userID);
            statement.setString(2, "");
            statement.executeUpdate();
            statement.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
            System.out.println("Não foi possível salvar um dado no banco de dados.");
        }
    }

    public void create(String id) {
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement("INSERT INTO " + tableMatches + "(id, data) VALUES(?,?)");
            statement.setString(1, id);
            statement.setString(2, "");
            statement.executeUpdate();
            statement.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
            System.out.println("Não foi possível salvar um dado no banco de dados.");
        }
    }

    public void close() {
        if (connection != null) {
            try {
                PlayerDataController.getDATA().forEach(this::save);

                connection.close();
                System.out.println("Conexão com o banco de dados foi fechada.");
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Não foi possivel fechar a conexão com o banco de dados.");
            }
        }
    }

    public boolean isNull() {
        return connection == null;
    }
}
