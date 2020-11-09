package com.yuhtin.lauren.tasks;

import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.core.player.SimplePlayer;
import com.yuhtin.lauren.core.player.controller.PlayerDatabase;
import com.yuhtin.lauren.database.DatabaseController;
import com.yuhtin.lauren.utils.helper.TaskHelper;
import com.yuhtin.lauren.utils.serialization.Serializer;
import lombok.Getter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class TopXpUpdater {
    private static final TopXpUpdater INSTANCE = new TopXpUpdater();
    @Getter private String topPlayers;

    public static TopXpUpdater getInstance() {
        return INSTANCE;
    }

    public void startRunnable() {
        Logger.log("Registered TopXpUpdater task");

        Connection connection = DatabaseController.get().getConnection();
        TaskHelper.runTaskTimerAsync(new TimerTask() {
            @Override
            public void run() {
                topPlayers = "";

                Logger.log("Started TopXpUpdater task");
                long lastMillis = System.currentTimeMillis();

                List<SimplePlayer> players = new ArrayList<>();

                String sql = "SELECT * FROM `lauren_players` ORDER BY `xp` DESC LIMIT 10";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    ResultSet resultSet = statement.executeQuery();

                    while (resultSet.next()) {
                        Player data = Serializer.getPlayer().deserialize(resultSet.getString("data"));
                        players.add(new SimplePlayer(resultSet.getLong("id"), data.getLevel(), data.getExperience()));
                    }

                    resultSet.close();

                } catch (Exception exception) {
                    Logger.log("Can't update top xp");
                    return;
                }

                StringBuilder builder = new StringBuilder();
                builder.append("<:version:756767328334512179> Top 10 jogadores mais viciados\n\n");

                for (int i = 1; i < players.size(); i++) {
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

                long ms = System.currentTimeMillis() - lastMillis;
                Logger.log("Finished TopXpUpdater task successfully in " + ms + " ms");

            }
        }, 0, 30, TimeUnit.MINUTES);

    }
}
