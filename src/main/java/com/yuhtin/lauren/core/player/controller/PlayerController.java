package com.yuhtin.lauren.core.player.controller;

import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.player.OldPlayer;
import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.utils.helper.TaskHelper;
import com.yuhtin.lauren.utils.serialization.player.PlayerSerializer;
import com.yuhtin.lauren.utils.serialization.Serializer;

import java.util.HashMap;
import java.util.Map;

public class PlayerController {

    public static final PlayerController INSTANCE = new PlayerController();
    private final Map<Long, Player> cache = new HashMap<>();

    public void savePlayers() {
        Logger.log("Saving all players, the bot may lag for a bit");

        TaskHelper.runAsync(() -> {
            cache.forEach(PlayerDatabase::save);
            cache.clear();
        });

        Logger.log("Saved all players");
    }

    public Player get(long userID) {
        Player player = cache.getOrDefault(userID, null);

        if (player == null) {

            String data = PlayerDatabase.loadPlayer(userID);
            if (data.equalsIgnoreCase("")) player = new Player(userID);

            else {

                Player deserialize = PlayerSerializer.deserialize(data);
                if (deserialize.getRank() == null) {
                    // execute conversion

                    Player tempPlayer = new Player(userID);
                    OldPlayer oldPlayer = Serializer.GSON.fromJson(data, OldPlayer.class);

                    tempPlayer.setExperience(oldPlayer.getExperience());
                    tempPlayer.setMoney(oldPlayer.getMoney());
                    tempPlayer.setLevel(oldPlayer.getLevel());

                    player = tempPlayer;
                    Logger.log("Converted data of player " + userID + " to new Player class");

                } else player = deserialize;

                Logger.log("Loading player " + userID + ": " + player.toString());
                if (deserialize.getPunishs() == null) deserialize.setPunishs(new HashMap<>());
            }

            cache.put(userID, player);

        }

        return player;
    }

    public int totalUsers() {
        return cache.size();
    }

}
