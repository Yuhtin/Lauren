package com.yuhtin.lauren.tasks;

import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.core.player.SimplePlayer;
import com.yuhtin.lauren.database.DatabaseController;
import com.yuhtin.lauren.utils.helper.TaskHelper;
import com.yuhtin.lauren.utils.serialization.Serializer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class TopXpUpdater {
    private static final TopXpUpdater INSTANCE = new TopXpUpdater();
    public String topPlayers;

    public TopXpUpdater() {
        startRunnable();
    }

    public static TopXpUpdater getInstance() {
        return INSTANCE;
    }

    private void startRunnable() {
        Logger.log("Started TopXpUpdater task");
        TaskHelper.runTaskTimerAsync(new TimerTask() {
            @Override
            public void run() {
                Connection connection = DatabaseController.get().getConnection();
                try {
                    PreparedStatement statement = connection.prepareStatement("SELECT * FROM `lauren_players` ORDER BY `xp` DESC LIMIT 10");
                    ResultSet resultSet = statement.executeQuery();

                    topPlayers = "";

                    List<SimplePlayer> players = new ArrayList<>();
                    while (resultSet.next()) {
                        Player data = Serializer.player.deserialize(resultSet.getString("data"));
                        players.add(new SimplePlayer(resultSet.getLong("id"), data.level, data.experience));
                    }

                    StringBuilder builder = new StringBuilder();
                    builder.append("<:version:756767328334512179> Top 10 jogadores mais viciados\n\n");

                    for (int i = 1; i <= 10; i++) {
                        SimplePlayer simplePlayer = players.get(i - 1);
                        builder.append(i)
                                .append("º - <@")
                                .append(simplePlayer.getUserID())
                                .append("> Nível ")
                                .append(simplePlayer.getLevel())
                                .append(" (")
                                .append(simplePlayer.getXp())
                                .append(" XP)\n");
                    }

                    topPlayers = builder.toString();
                    players.clear();
                    resultSet.close();
                    statement.close();
                } catch (Exception exception) {
                    Logger.error(exception);
                    Logger.log("Cannot update top xp");
                }
            }
        }, 0, 30, TimeUnit.MINUTES);

        Logger.log("Finished TopXpUpdater task successfully");
    }
}
