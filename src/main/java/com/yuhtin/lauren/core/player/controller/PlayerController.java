package com.yuhtin.lauren.core.player.controller;

import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.sql.dao.PlayerDAO;
import com.yuhtin.lauren.utils.helper.TaskHelper;
import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Singleton
public class PlayerController {

    @Getter @Inject private PlayerDAO playerDAO;
    @Inject @Named("main") private Logger logger;

    private final Map<Long, Player> cache = new HashMap<>();

    public void savePlayers() {
        this.logger.info("Saving all players, the bot may lag for a bit");

        TaskHelper.runAsync(() -> {
            this.cache.values().forEach(this.playerDAO::updatePlayer);
            this.cache.clear();
        });

        this.logger.info("Saved all players");
    }

    public Player get(long userID) {
        Player player = cache.getOrDefault(userID, null);

        if (player == null) {

            Player loadedPlayer = this.playerDAO.findById(userID);
            if (loadedPlayer == null) {

                player = new Player(userID);
                this.playerDAO.insertPlayer(player);

            } else {

                if (loadedPlayer.getRank() == null) {
                    // execute conversion

                    player = new Player(userID);

                    player.setExperience(loadedPlayer.getExperience());
                    player.setMoney(loadedPlayer.getMoney());
                    player.setLevel(loadedPlayer.getLevel());

                    this.logger.info("Converted data of player " + userID + " to new Player class");

                } else player = loadedPlayer;

                this.logger.info("Loading player " + userID);
                if (loadedPlayer.getPunishs() == null) loadedPlayer.setPunishs(new HashMap<>());
            }

            this.cache.put(userID, player);

        }

        return player;
    }

    public int totalUsers() {
        return cache.size();
    }

}
