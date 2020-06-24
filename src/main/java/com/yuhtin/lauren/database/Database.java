package com.yuhtin.lauren.database;

import com.yuhtin.lauren.core.logger.LogType;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.match.Match;
import com.yuhtin.lauren.core.match.controller.MatchController;
import com.yuhtin.lauren.core.player.PlayerData;
import com.yuhtin.lauren.utils.serialization.Serializer;

import javax.annotation.Nullable;
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
            e.printStackTrace();
            Logger.log("Database tables could not be created", LogType.ERROR);
            return false;
        }
    }

    public boolean loadData() {
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement("SELECT * FROM " + tableMatches);
            ResultSet query = statement.executeQuery();
            while (query.next()) {
                MatchController.insert(Serializer.match.deserialize(query.getString("data")));
            }
            statement.close();

            return true;
        } catch (SQLException exception) {
            Logger.log("Could not load data from database", LogType.ERROR).save();
            return false;
        }
    }

    @Nullable
    public String loadPlayer(Long userID) {
        PreparedStatement statement;
        String data = null;
        try {
            statement = connection.prepareStatement("SELECT * FROM " + tablePlayers + " WHERE `id` = ?");
            statement.setLong(1, userID);

            ResultSet query = statement.executeQuery();
            if (query.getFetchSize() > 1) Logger.log("I found multiple values for id " + userID, LogType.WARN).save();

            while (query.next()) {
                data = query.getString("data");
            }
            statement.close();

            return data;
        } catch (SQLException exception) {
            Logger.log("Could not load player from database", LogType.ERROR).save();
            return null;
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
            Logger.log("Could not save data to database", LogType.ERROR).save();
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
            Logger.log("Could not save data to database", LogType.ERROR).save();
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
            Logger.log("Could not create data in database", LogType.ERROR).save();
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
            Logger.log("Could not create data in database", LogType.ERROR).save();
        }
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
                Logger.log("Connection to the database has been closed", LogType.LOG).save();
            } catch (SQLException exception) {
                Logger.log("Could not close the connection to the database", LogType.ERROR).save();
            }
        }
    }

    public boolean isNull() {
        return connection == null;
    }
}
