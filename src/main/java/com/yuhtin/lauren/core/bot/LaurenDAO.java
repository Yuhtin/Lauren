package com.yuhtin.lauren.core.bot;

import com.google.inject.Injector;

import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.models.enums.LogType;
import com.yuhtin.lauren.models.exceptions.GuiceInjectorException;
import com.yuhtin.lauren.models.exceptions.SQLConnectionException;
import com.yuhtin.lauren.models.objects.Config;
import com.yuhtin.lauren.sql.connection.ConnectionInfo;
import com.yuhtin.lauren.sql.connection.SQLConnection;
import com.yuhtin.lauren.sql.connection.mysql.MySQLConnection;
import com.yuhtin.lauren.startup.Startup;
import com.yuhtin.lauren.utils.helper.EventWaiter;
import lombok.Data;
import net.dv8tion.jda.api.JDA;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.Properties;

@Data
public abstract class LaurenDAO implements Bot {

    private final Logger logger = new Logger();
    private final EventWaiter eventWaiter = new EventWaiter();
    private final SQLConnection sqlConnection = new MySQLConnection();

    private String botName;
    private JDA bot;
    private Injector injector;
    private Config config;

    private String version;
    private long botStartTime;

    /**
     * Called on bot starting
     * Used for enable high important systems
     */
    public void onLoad() throws Exception {

    }

    /**
     * Called on bot starting after onLoad
     */
    public void onEnable() throws Exception {
    }

    /**
     * Called on bot connect to discord
     */
    public void onReady() {
    }

    /**
     * Called on bot disabled (forced and natural)
     */
    public void onDisable() throws Exception {
    }

    @Override
    public void shutdown() {

        try { onDisable(); } catch (Exception exception) {

            this.logger.log(LogType.SEVERE, "Can't run onDisable, shutdown cancelled", exception);
            return;

        }

        System.exit(0);

    }

    @Override
    public void connectDiscord() throws LoginException {

    }

    @Override
    public void setupConfig() throws InstantiationException {

        this.config = Config.loadConfig("config/config.json");
        if (config == null) throw new InstantiationException("Config created, configure token");

    }

    public void setupGuice() throws GuiceInjectorException {

    }

    @Override
    public void configureConnection() throws SQLConnectionException {

        ConnectionInfo connectionInfo = ConnectionInfo.builder()
                .database(this.config.getDatabase())
                .password(this.config.getPassword())
                .host(this.config.getHost())
                .username(this.config.getUsername())
                .build();

        if (!this.sqlConnection.configure(connectionInfo)) throw new SQLConnectionException();

    }

    @Override
    public void findVersion() throws IOException {

        Properties properties = new Properties();

        properties.load(Startup.class.getClassLoader().getResourceAsStream("project.properties"));
        this.version = properties.getProperty("version");

    }
}
