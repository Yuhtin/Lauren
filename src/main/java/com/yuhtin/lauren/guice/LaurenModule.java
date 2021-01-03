package com.yuhtin.lauren.guice;

import com.google.inject.AbstractModule;
import com.yuhtin.lauren.core.bot.LaurenDAO;
import com.yuhtin.lauren.sql.connection.SQLConnection;
import lombok.AllArgsConstructor;

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
                .toInstance(laurenDAO);

        bind(SQLConnection.class)
                .toInstance(laurenDAO.getSqlConnection());

    }


}
