package com.yuhtin.lauren.core.bot;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.yuhtin.lauren.core.logger.LogFormat;
import com.yuhtin.lauren.guice.LaurenModule;
import com.yuhtin.lauren.models.exceptions.GuiceInjectorException;
import com.yuhtin.lauren.models.exceptions.SQLConnectionException;
import com.yuhtin.lauren.models.objects.Config;
import com.yuhtin.lauren.sql.connection.ConnectionInfo;
import com.yuhtin.lauren.sql.connection.SQLConnection;
import com.yuhtin.lauren.sql.connection.mysql.MySQLConnection;
import com.yuhtin.lauren.sql.connection.sqlite.SQLiteConnection;
import com.yuhtin.lauren.startup.Startup;
import com.yuhtin.lauren.utils.helper.InfinityFiles;
import com.yuhtin.lauren.utils.helper.Utilities;
import lombok.Data;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

@Data
@Singleton
public abstract class LaurenDAO implements Bot {

    private String botName;

    private ShardManager bot;

    private Logger logger;
    private Injector injector;
    private SQLConnection sqlConnection;
    private Config config;
    private EventWaiter eventWaiter = new EventWaiter();

    private String logFile;
    private String version;
    private long botStartTime;
    private boolean loaded;

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

            this.logger.log(Level.SEVERE, "Can't run onDisable, shutdown cancelled", exception);
            return;

        }

        System.exit(0);

    }

    @Override
    public void connectDiscord() throws LoginException {

        this.bot = DefaultShardManagerBuilder.createDefault(this.config.getToken())
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setChunkingFilter(ChunkingFilter.ALL)
                .enableIntents(Arrays.asList(GatewayIntent.values()))
                .setAutoReconnect(true)
                .build();

    }

    @Override
    public void setupLogger() throws IOException {

        this.logger = Logger.getLogger(botName);
        if (this.config.isLog()) {

            InfinityFiles infinityFiles = new InfinityFiles("log", "logs", ".log", ".zip");

            logFile = infinityFiles.getNextFile();

            FileHandler file = new FileHandler(logFile);
            file.setFormatter(new LogFormat());

            this.logger.addHandler(file);

        }

        this.logger.info("Logger setup successfully");

    }

    @Override
    public void setupConfig() throws InstantiationException {

        this.config = Config.loadConfig("config/config.json");
        if (config == null) throw new InstantiationException("Config created, configure token");

    }

    public void setupGuice() throws GuiceInjectorException {

        try {

            this.injector = Guice.createInjector(new LaurenModule(this));
            this.injector.injectMembers(this);
            this.injector.injectMembers(Utilities.INSTANCE);

        } catch (Exception exception) {
            throw new GuiceInjectorException();
        }

    }

    @Override
    public void configureConnection() throws SQLConnectionException {

        ConnectionInfo connectionInfo = ConnectionInfo.builder()
                .file(this.config.getSqlFile())
                .database(this.config.getDatabase())
                .password(this.config.getPassword())
                .host(this.config.getHost())
                .username(this.config.getUsername())
                .build();

        if (this.config.getDatabaseType().equalsIgnoreCase("MySQL"))
            this.sqlConnection = new MySQLConnection();
        else this.sqlConnection = new SQLiteConnection();

        if (!this.sqlConnection.configure(connectionInfo)) throw new SQLConnectionException();

        this.logger.info("Connection to database successful");

    }

    @Override
    public void findVersion() throws IOException {

        Properties properties = new Properties();

        properties.load(Startup.class.getClassLoader().getResourceAsStream("project.properties"));
        this.version = properties.getProperty("version");

    }
}
