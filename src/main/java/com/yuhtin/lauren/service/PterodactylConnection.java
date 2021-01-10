package com.yuhtin.lauren.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.stanjg.ptero4j.PteroUserAPI;
import com.stanjg.ptero4j.entities.panel.user.UserServer;
import com.yuhtin.lauren.core.statistics.StatsController;
import lombok.Getter;

@Singleton
public class PterodactylConnection {

    @Inject private StatsController statsController;

    @Getter private PteroUserAPI connection;
    @Getter private UserServer server;

    public void load(String apiKey) {

        connection = new PteroUserAPI("https://minecraft.hypehost.com.br/", apiKey);

        server = connection.getServersController().getServer("bb59eaa0");
        this.statsController.getStats("Requests Externos").suplyStats(1);

    }

}
