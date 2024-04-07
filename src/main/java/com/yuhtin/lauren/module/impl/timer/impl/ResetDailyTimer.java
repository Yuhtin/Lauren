package com.yuhtin.lauren.module.impl.timer.impl;

import com.yuhtin.lauren.database.MongoOperation;
import com.yuhtin.lauren.database.OperationFilter;
import com.yuhtin.lauren.module.Module;
import com.yuhtin.lauren.module.impl.player.Player;
import com.yuhtin.lauren.module.impl.player.module.PlayerModule;
import com.yuhtin.lauren.module.impl.timer.Timer;
import com.yuhtin.lauren.util.LoggerUtil;

import java.util.List;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

public class ResetDailyTimer implements Timer {

    @Override
    public String name() {
        return "Reset Daily";
    }

    @Override
    public String day() {
        return "ALL";
    }

    @Override
    public List<String> hours() {
        return List.of("12:00");
    }

    @Override
    public void run() {
        PlayerModule playerModule = Module.instance(PlayerModule.class);
        if (playerModule == null) return;

        playerModule.saveAll().queue(completed -> {
            MongoOperation.bind(Player.class)
                    .filter(OperationFilter.EQUALS, "abbleToDaily", false)
                    .findMany()
                    .queue(players -> {
                        for (Player player : players) {
                            player.setAbbleToDaily(true);
                            player.save();
                        }

                        LoggerUtil.getLogger().info("Daily reseted for " + players.size() + " players!");
                    });
        });
    }
}
