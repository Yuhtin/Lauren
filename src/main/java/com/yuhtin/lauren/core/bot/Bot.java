package com.yuhtin.lauren.core.bot;

import com.yuhtin.lauren.models.exceptions.SQLConnectionException;

import javax.security.auth.login.LoginException;
import java.io.IOException;

public interface Bot {

    void connectDiscord() throws LoginException;

    void setupLogger() throws IOException;

    void setupConfig() throws InstantiationException;

    void configureConnection() throws SQLConnectionException;

    void loadCommands() throws IOException;

    void loadEvents() throws IOException;

    void findVersion(Class<Object> oClass) throws IOException;

    void shutdown() throws Exception;

}
