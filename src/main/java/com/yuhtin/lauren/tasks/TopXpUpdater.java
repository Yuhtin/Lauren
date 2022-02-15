package com.yuhtin.lauren.tasks;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.player.SimplePlayer;
import com.yuhtin.lauren.core.player.serializer.PlayerSerializer;
import com.yuhtin.lauren.models.enums.LogType;
import com.yuhtin.lauren.sql.connection.SQLConnection;
import com.yuhtin.lauren.utils.TaskHelper;
import lombok.Getter;
import lombok.val;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

@Singleton
public class TopXpUpdater {

    @Inject private Logger logger;
    @Inject private SQLConnection sqlConnection;

    @Getter private String topPlayers;

    public void startRunnable() {
        logger.info("Registered TopXpUpdater task");

        val connection = sqlConnection.findConnection();
        TaskHelper.runTaskTimerAsync(new TimerTask() {
            @Override
            public void run() {

                topPlayers = "";

                logger.info("Started TopXpUpdater task");
                val lastMillis = System.currentTimeMillis();

                val players = new ArrayList<SimplePlayer>();

                val sql = "SELECT * FROM `lauren_players` ORDER BY `xp` DESC LIMIT 10";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    val resultSet = statement.executeQuery();

                    while (resultSet.next()) {
                        val data = PlayerSerializer.deserialize(resultSet.getString("data"));
                        players.add(new SimplePlayer(resultSet.getLong("id"), data.getLevel(), resultSet.getInt("xp")));
                    }

                    resultSet.close();

                } catch (Exception exception) {
                    logger.log(LogType.WARNING, "Can't update top xp", exception);
                    return;
                }

                val builder = new StringBuilder();
                builder.append("<:version:756767328334512179> Top 10 jogadores mais viciados\n\n");

                for (int i = 1; i < players.size(); i++) {
                    val simplePlayer = players.get(i - 1);
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

                val ms = System.currentTimeMillis() - lastMillis;
                logger.info("Finished TopXpUpdater task successfully in " + ms + " ms");

            }
        }, 0, 30, TimeUnit.MINUTES);

    }
}
