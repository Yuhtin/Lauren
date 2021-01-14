package com.yuhtin.lauren.guice;

import com.google.inject.AbstractModule;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.yuhtin.lauren.Lauren;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.music.AudioResultHandler;
import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.models.objects.Config;
import com.yuhtin.lauren.service.GetConnectionFactory;
import com.yuhtin.lauren.service.PostConnectionFactory;
import com.yuhtin.lauren.sql.connection.SQLConnection;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.sharding.ShardManager;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@AllArgsConstructor
public class LaurenModule extends AbstractModule {

    private final Lauren lauren;

    @Override
    protected void configure() {

        bind(Lauren.class)
                .toInstance(this.lauren);

        bind(ShardManager.class)
                .toInstance(this.lauren.getBot());

        bind(SQLConnection.class)
                .toInstance(this.lauren.getSqlConnection());

        bind(EventWaiter.class)
                .toInstance(this.lauren.getEventWaiter());

        bind(Logger.class)
                .toInstance(this.lauren.getLogger());

        bind(Config.class)
                .toInstance(this.lauren.getConfig());

        requestStaticInjection(GetConnectionFactory.class);
        requestStaticInjection(AudioResultHandler.class);
        requestStaticInjection(Player.class);
        requestStaticInjection(PostConnectionFactory.class);
        requestStaticInjection(Logger.class);

    }


}
