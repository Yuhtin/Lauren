package com.yuhtin.lauren.guice;

import com.google.inject.AbstractModule;
import com.yuhtin.lauren.Lauren;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.module.impl.music.MusicSearchEngine;
import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.service.GetConnectionFactory;
import com.yuhtin.lauren.service.PostConnectionFactory;
import com.yuhtin.lauren.sql.connection.SQLConnection;
import lombok.AllArgsConstructor;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@AllArgsConstructor
public class LaurenModule extends AbstractModule {

    private final Lauren lauren;

    @Override
    protected void configure() {
        bind(Lauren.class).toInstance(lauren);
        bind(SQLConnection.class).toInstance(lauren.getSqlConnection());
        bind(EventWaiter.class).toInstance(lauren.getEventWaiter());
        bind(Logger.class).toInstance(lauren.getLogger());
        bind(Config.class).toInstance(lauren.getConfig());

        requestStaticInjection(GetConnectionFactory.class);
        requestStaticInjection(MusicSearchEngine.class);
        requestStaticInjection(Player.class);
        requestStaticInjection(PostConnectionFactory.class);
        requestStaticInjection(UserUtil.class);
    }


}
