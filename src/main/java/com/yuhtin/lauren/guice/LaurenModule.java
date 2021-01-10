package com.yuhtin.lauren.guice;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.yuhtin.lauren.core.bot.LaurenDAO;
import com.yuhtin.lauren.core.music.AudioResultHandler;
import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.models.objects.Config;
import com.yuhtin.lauren.service.GetConnectionFactory;
import com.yuhtin.lauren.service.PostConnectionFactory;
import com.yuhtin.lauren.sql.connection.SQLConnection;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.logging.Logger;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@AllArgsConstructor
public class LaurenModule extends AbstractModule {

    private final LaurenDAO laurenDAO;

    @Override
    protected void configure() {

        bind(LaurenDAO.class)
                .toInstance(this.laurenDAO);

        bind(ShardManager.class)
                .toInstance(this.laurenDAO.getBot());

        bind(SQLConnection.class)
                .toInstance(this.laurenDAO.getSqlConnection());

        bind(EventWaiter.class)
                .toInstance(this.laurenDAO.getEventWaiter());

        bind(Logger.class)
                .annotatedWith(Names.named("main"))
                .toInstance(this.laurenDAO.getLogger());

        bind(Config.class)
                .toInstance(this.laurenDAO.getConfig());

        requestStaticInjection(GetConnectionFactory.class);
        requestStaticInjection(AudioResultHandler.class);
        requestStaticInjection(Player.class);
        requestStaticInjection(PostConnectionFactory.class);

    }


}
