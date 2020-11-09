package com.yuhtin.lauren.service;

import com.stanjg.ptero4j.PteroUserAPI;
import com.stanjg.ptero4j.entities.panel.user.UserServer;
import com.yuhtin.lauren.core.statistics.controller.StatsController;
import lombok.Getter;

public class PterodactylConnection {

    private static PterodactylConnection instance;
    @Getter private final PteroUserAPI connection;
    @Getter private final UserServer server;

    public PterodactylConnection(String apiKey) {
        instance = this;
        connection = new PteroUserAPI("https://minecraft.hypehost.com.br/", apiKey);

        server = connection.getServersController().getServer("bb59eaa0");
        StatsController.get().getStats("Requests Externos").suplyStats(1);
    }

    public static PterodactylConnection get() {
        return instance;
    }
}
