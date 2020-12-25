package com.yuhtin.lauren.sql.connection;

import lombok.Builder;
import lombok.Data;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@Data
@Builder
public class ConnectionInfo {

    private final boolean mysqlEnabled;

    private final String host;
    private final String database;
    private final String username;
    private final String password;
    private final String file;


}
