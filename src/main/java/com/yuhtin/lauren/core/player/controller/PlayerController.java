package com.yuhtin.lauren.core.player.controller;

import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.player.OldPlayer;
import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.utils.helper.TaskHelper;
import com.yuhtin.lauren.utils.serialization.Serializer;

import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class PlayerController {

    public static final PlayerController INSTANCE = new PlayerController();
    private final Map<Long, Player> cache = new HashMap<>();

    public void savePlayers() {
        Logger.log("Saving all players, the bot may lag for a bit").save();

        TaskHelper.runAsync(() -> cache.forEach(PlayerDatabase::save));
        TaskHelper.runTaskLater(new TimerTask() {
            @Override
            public void run() {
                cache.clear();
            }
        }, 10, TimeUnit.SECONDS);

        Logger.log("Saved all players").save();
    }

    public Player get(long userID) {
        Player player = cache.getOrDefault(userID, null);

        if (player == null) {

            String data = PlayerDatabase.loadPlayer(userID);

            if (data.equalsIgnoreCase("")) player = new Player(userID);

            else {

                Player deserialize = Serializer.getPlayer().deserialize(data);
                if (deserialize.rank == null) {
                    // execute conversion

                    Player tempPlayer = new Player(userID);
                    OldPlayer oldPlayer = Serializer.GSON.fromJson(data, OldPlayer.class);

                    tempPlayer.setExperience(oldPlayer.getExperience());
                    tempPlayer.setMoney(oldPlayer.getMoney());
                    tempPlayer.setLevel(oldPlayer.getLevel());
                    tempPlayer.setDailyDelay(oldPlayer.getDailyDelay());

                    player = tempPlayer;
                    Logger.log("Converted data of player " + userID + " to new Player class").save();

                }else player = deserialize;

            }

            cache.put(userID, player);

        }

        return player;
    }

}
