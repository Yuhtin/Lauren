package com.yuhtin.lauren.database;

import com.yuhtin.lauren.models.data.PlayerData;
import com.yuhtin.lauren.models.cache.PlayerDataCache;
import com.yuhtin.lauren.models.data.Match;
import com.yuhtin.lauren.models.cache.MatchCache;
import com.yuhtin.lauren.utils.serialization.Serializer;

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
                    "CREATE TABLE IF NOT EXISTS " + tablePlayers + " (`id` LONG NOT NULL, `data` TEXT);");
            statement.executeUpdate();
            statement.close();

            statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + tableMatches + " (`id` VARCHAR(15) PRIMARY KEY NOT NULL, `data` TEXT);");
            statement.executeUpdate();
            statement.close();
            return true;
        } catch (SQLException e) {
            System.out.println("The table could not be created");
            e.printStackTrace();
            return false;
        }
    }

    public boolean loadData() {
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement("SELECT * FROM " + tablePlayers);

            ResultSet result = statement.executeQuery();
            while (result.next()) {
                PlayerDataCache.insert(Serializer.playerData.deserialize(result.getString("data")));
            }
            statement.close();

            statement = connection.prepareStatement("SELECT * FROM " + tableMatches);
            result = statement.executeQuery();
            while (result.next()) {
                MatchCache.insert(Serializer.match.deserialize(result.getString("data")));
            }
            statement.close();

            return true;
        } catch (SQLException exception) {
            exception.printStackTrace();
            System.out.println("Could not save data to database");
            return false;
        }
    }

    public void save(Long userID, PlayerData controller) {
        PreparedStatement statement;
        try {
            String result = Serializer.playerData.serialize(controller);
            statement = connection.prepareStatement("UPDATE " + tablePlayers + " SET `data` = ? WHERE `id` = ?");
            statement.setString(1, result);
            statement.setLong(2, userID);

            statement.executeUpdate();
            statement.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
            System.out.println("Could not save data to database");
        }
    }

    public void save(String id, Match match) {
        PreparedStatement statement;
        try {
            String result = Serializer.match.serialize(match);
            statement = connection.prepareStatement("UPDATE " + tableMatches + " SET `data` = ? WHERE `id` = ?");
            statement.setString(1, result);
            statement.setString(2, id);

            statement.executeUpdate();
            statement.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
            System.out.println("Could not save data to database");
        }
    }

    public void create(Long userID) {
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement("INSERT INTO " + tablePlayers + " (`id`, `data`) VALUES(?,?)");
            statement.setLong(1, userID);
            statement.setString(2, Serializer.playerData.serialize(new PlayerData(userID)));

            statement.executeUpdate();
            statement.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
            System.out.println("Could not save data to database");
        }
    }

    public void create(String id) {
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement("INSERT INTO " + tableMatches + "(`id`, `data`) VALUES(?,?)");
            statement.setString(1, id);
            statement.setString(2, "");

            statement.executeUpdate();
            statement.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
            System.out.println("Could not save data to database");
        }
    }

    public void close() {
        if (connection != null) {
            try {
                PlayerDataCache.getDATA().forEach(this::save);

                connection.close();
                System.out.println("Connection to the database has been closed");
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Could not close the connection to the database");
            }
        }
    }

    public boolean isNull() {
        return connection == null;
    }
}
