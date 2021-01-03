package com.yuhtin.lauren.core.bot;

import java.io.IOException;

public interface Bot {

    boolean connectDiscord();

    boolean setupLogger();

    boolean setupConfig();

    boolean setupGuice();

    boolean configureConnection();

    boolean findVersion(Class<Object> oClass);

}
