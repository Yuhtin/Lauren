package com.yuhtin.lauren;

import com.yuhtin.lauren.core.bot.LaurenDAO;

public class Lauren extends LaurenDAO {

    @Override
    public void onEnable() {

        this.findVersion(Lauren.class);
        this.setBotStartTime(System.currentTimeMillis());
        this.setupConfig();
        this.setupLogger();

        this.configureConnection();
        this.connectDiscord();

    }

    @Override
    public void onReady() {

        this.getLogger().info("Lauren is now ready");

    }

    @Override
    public void onDisable() {

        this.getLogger().info("Lauren disabled");

    }
}
