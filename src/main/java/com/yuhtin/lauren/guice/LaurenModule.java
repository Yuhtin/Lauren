package com.yuhtin.lauren.guice;

import com.google.inject.AbstractModule;
import com.yuhtin.lauren.Lauren;
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

        bind(Lauren.class)
                .toInstance(lauren);

        bind(SQLConnection.class)
                .toInstance(lauren.getSqlConnection());

    }


}
