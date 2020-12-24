package com.yuhtin.lauren.timers.impl;

import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.core.player.controller.PlayerDatabase;
import com.yuhtin.lauren.database.DatabaseController;
import com.yuhtin.lauren.timers.Timer;
import com.yuhtin.lauren.utils.serialization.player.PlayerSerializer;
import lombok.Getter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.TimeUnit;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class ResetDailyTimer implements Timer {

    @Getter private static final ResetDailyTimer instance = new ResetDailyTimer();

    @Getter private long nextReset;

    @Override
    public String name() {
        return "Reset Daily";
    }

    @Override
    public String day() {
        return "ALL";
    }

    @Override
    public int hour() {
        return 12;
    }

    @Override
    public int minute() {
        return 0;
    }

    @Override
    public void run() {

        nextReset = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1);
        PlayerController.INSTANCE.savePlayers();

        Connection connection = DatabaseController.get().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM `lauren_players`"
        )) {

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {

                long id = resultSet.getLong("id");

                Player player = PlayerSerializer.deserialize(resultSet.getString("data"));
                player.setAbbleToDaily(true);

                PlayerDatabase.save(id, player);

            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }
}
