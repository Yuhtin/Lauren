package com.yuhtin.lauren.module.impl.leaderboard;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Sorts;
import com.yuhtin.lauren.Lauren;
import com.yuhtin.lauren.database.MongoOperation;
import com.yuhtin.lauren.module.Module;
import com.yuhtin.lauren.module.impl.player.Player;
import com.yuhtin.lauren.util.LoggerUtil;
import com.yuhtin.lauren.util.TaskHelper;
import lombok.Getter;
import org.bson.Document;

import java.util.concurrent.TimeUnit;

@Getter
public class ExperiencieLeaderboardModule implements Module {

    private String leaderboard = "";

    @Override
    public boolean setup(Lauren lauren) {
        TaskHelper.runTaskTimerAsync(this::updateLeaderboard, 10, 30, TimeUnit.MINUTES);
        return true;
    }

    private void updateLeaderboard() {
        LoggerUtil.getLogger().info("Updating experience leaderboard...");

        long millis = System.currentTimeMillis();

        MongoOperation.bind(Player.class)
                .and(Aggregates.sort(Sorts.descending("experience")))
                .and(Aggregates.limit(10))
                .executeMany()
                .queue(players -> {
                    StringBuilder builder = new StringBuilder();
                    builder.append("<:version:756767328334512179> Top 10 jogadores mais viciados\n\n");

                    for (int i = 1; i < players.size(); i++) {
                        Document document = players.get(i - 1);
                        builder.append(i)
                                .append("º - <@")
                                .append(document.getLong("id"))
                                .append("> Nível ")
                                .append(document.getInteger("level"))
                                .append(" (")
                                .append(document.getInteger("experience"))
                                .append(" XP)\n");
                    }

                    leaderboard = builder.toString();

                    long ms = System.currentTimeMillis() - millis;
                    LoggerUtil.getLogger().info("Updated experience leaderboard in " + ms + " ms");
                });
    }

}
