package com.yuhtin.lauren.tasks;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.core.player.SimplePlayer;
import com.yuhtin.lauren.models.enums.LogType;
import com.yuhtin.lauren.sql.connection.SQLConnection;
import com.yuhtin.lauren.utils.helper.TaskHelper;
import com.yuhtin.lauren.utils.serialization.player.PlayerSerializer;
import lombok.Getter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

@Singleton
public class TopXpUpdater {

    @Inject private Logger logger;
    @Inject private SQLConnection sqlConnection;

    @Getter private String topPlayers;

    public void startRunnable() {

        logger.info("Registered TopXpUpdater task");

        Connection connection = this.sqlConnection.findConnection();
        TaskHelper.runTaskTimerAsync(new TimerTask() {
            @Override
            public void run() {

                topPlayers = "";

                logger.info("Started TopXpUpdater task");
                long lastMillis = System.currentTimeMillis();

                List<SimplePlayer> players = new ArrayList<>();

                String sql = "SELECT * FROM `lauren_players` ORDER BY `xp` DESC LIMIT 10";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    ResultSet resultSet = statement.executeQuery();

                    while (resultSet.next()) {
                        Player data = PlayerSerializer.deserialize(resultSet.getString("data"));
                        players.add(new SimplePlayer(resultSet.getLong("id"), data.getLevel(), data.getExperience()));
                    }

                    resultSet.close();

                } catch (Exception exception) {
                    logger.log(LogType.WARNING, "Can't update top xp", exception);
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
                logger.info("Finished TopXpUpdater task successfully in " + ms + " ms");

            }
        }, 0, 30, TimeUnit.MINUTES);

    }
}
