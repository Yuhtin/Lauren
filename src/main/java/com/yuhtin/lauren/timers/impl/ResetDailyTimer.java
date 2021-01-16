package com.yuhtin.lauren.timers.impl;

import com.google.inject.Inject;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.timers.Timer;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

public class ResetDailyTimer implements Timer {

    @Inject private PlayerController playerController;

    @Override
    public String name() {
        return "Reset Daily";
    }

    @Override
    public String day() {
        return "ALL";
    }

    @Override
    public int hour() {
        return 12;
    }

    @Override
    public int minute() {
        return 0;
    }

    @Override
    public void run() {

        this.playerController.savePlayers();
        this.playerController.getPlayerDAO().updateAllDailys();

    }
}
