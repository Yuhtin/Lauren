package com.yuhtin.lauren.guice;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.yuhtin.lauren.core.bot.LaurenDAO;
import com.yuhtin.lauren.sql.connection.SQLConnection;
import lombok.AllArgsConstructor;

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

        bind(SQLConnection.class)
                .toInstance(this.laurenDAO.getSqlConnection());

        bind(Logger.class)
                .annotatedWith(Names.named("main"))
                .toInstance(this.laurenDAO.getLogger());

    }


}
