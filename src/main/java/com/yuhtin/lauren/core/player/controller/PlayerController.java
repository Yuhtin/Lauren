package com.yuhtin.lauren.core.player.controller;

import com.yuhtin.lauren.core.alarm.controller.AlarmController;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.utils.helper.TaskHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class PlayerController {

    public static final PlayerController INSTANCE = new PlayerController();
    private final Map<Long, Player> cache = new HashMap<>();

    public void savePlayers() {
        Logger.log("Saving all players, the bot may lag for a bit").save();
        TaskHelper.runAsync(new Thread(() -> cache.forEach(PlayerDatabase::save)));
        TaskHelper.runTaskLater(new TimerTask() {
            @Override
            public void run() {
                cache.clear();
            }
        }, 5, TimeUnit.SECONDS);
        Logger.log("Saved all players").save();
    }

    public Player get(long userID) {
        Player player = cache.getOrDefault(userID, null);

        if (player == null) {
            player = PlayerDatabase.loadPlayer(userID);
            cache.put(userID, player);
        } else {
            for (String name : AlarmController.get().getAlarms().keySet()) {
                for (String alarmName : player.alarmsName) {
                    if (!alarmName.equals(name)) continue;

                    player.alarms.add(AlarmController.get().getAlarms().get(name));
                    player.alarmsName.remove(alarmName);
                }
            }
        }

        return player;
    }

}
