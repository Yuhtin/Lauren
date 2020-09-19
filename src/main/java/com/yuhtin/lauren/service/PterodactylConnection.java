package com.yuhtin.lauren.service;

import com.stanjg.ptero4j.PteroUserAPI;
import com.stanjg.ptero4j.entities.panel.user.UserServer;
import lombok.Getter;

public class PterodactylConnection {

    private static PterodactylConnection INSTANCE;
    @Getter
    private final PteroUserAPI connection;
    @Getter
    private final UserServer server;

    public PterodactylConnection(String apiKey) {
        INSTANCE = this;
        connection = new PteroUserAPI("https://minecraft.hypehost.com.br/", apiKey);

        server = connection.getServersController().getServer("bb59eaa0");
    }

    public static PterodactylConnection get() {
        return INSTANCE;
    }
}
