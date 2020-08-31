package com.yuhtin.lauren.service;

import com.yuhtin.lauren.application.Lauren;
import com.yuhtin.lauren.core.player.Player;

import java.util.HashMap;
import java.util.Map;

public class PlayerService {

    public static final PlayerService INSTANCE = new PlayerService();
    private final Map<Long, Player> cache = new HashMap<>();

    public void savePlayers() {cache.forEach((id, user) -> Lauren.data.save(id, user));}

    public Player get(long userID) {
        Player player = cache.getOrDefault(userID, null);

        if (player == null) {
            player = Lauren.data.loadPlayer(userID);
            cache.put(userID, player);
        }

        return player;
    }

}
