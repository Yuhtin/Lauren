package com.yuhtin.lauren.core.player.controller;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.sql.dao.PlayerDAO;
import com.yuhtin.lauren.utils.TaskHelper;
import lombok.Getter;
import lombok.val;
import lombok.var;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class PlayerController {

    @Getter @Inject private PlayerDAO playerDAO;
    @Inject private Logger logger;

    private final Map<Long, Player> cache = new HashMap<>();

    public void savePlayers() {
        logger.info("Saving all players, the bot may lag for a bit");

        TaskHelper.runAsync(() -> {
            cache.values().forEach(playerDAO::updatePlayer);
            cache.clear();
        });

        logger.info("Saved all players");
    }

    public Player get(long userID) {
        var player = cache.getOrDefault(userID, null);

        if (player == null) {

            val loadedPlayer = playerDAO.findById(userID);
            if (loadedPlayer == null) {

                player = new Player(userID);
                playerDAO.insertPlayer(player);

            } else {

                if (loadedPlayer.getRank() == null) {
                    // execute conversion

                    player = new Player(userID);

                    player.setExperience(loadedPlayer.getExperience());
                    player.setMoney(loadedPlayer.getMoney());
                    player.setLevel(loadedPlayer.getLevel());

                    logger.info("Converted data of player " + userID + " to new Player class");

                } else player = loadedPlayer;

                logger.info("Loading player " + userID);
                if (loadedPlayer.getPunishs() == null) loadedPlayer.setPunishs(new HashMap<>());
            }

            cache.put(userID, player);

        }

        return player;
    }

    public int totalUsers() {
        return cache.size();
    }

}
