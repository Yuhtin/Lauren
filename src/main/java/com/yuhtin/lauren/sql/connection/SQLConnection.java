package com.yuhtin.lauren.sql.connection;

import java.sql.Connection;
/**
 * @author Henry Fábio
 * Github: https://github.com/HenryFabio
 */
public interface SQLConnection {

    boolean configure(ConnectionInfo info);

    Connection findConnection();

}
