package com.yuhtin.lauren.timers.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.timers.Timer;
import lombok.Getter;

import java.util.concurrent.TimeUnit;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@Singleton
public class ResetDailyTimer implements Timer {

    @Inject private PlayerController playerController;

    @Getter private long nextReset;

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

        this.nextReset = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1);
        this.playerController.getPlayerDAO().updateAllDailys();

    }
}
