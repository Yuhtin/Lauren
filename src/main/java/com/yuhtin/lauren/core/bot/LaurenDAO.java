package com.yuhtin.lauren.core.bot;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.yuhtin.lauren.LaurenStartup;
import com.yuhtin.lauren.core.logger.LogFormat;
import com.yuhtin.lauren.guice.LaurenModule;
import com.yuhtin.lauren.models.enums.LogType;
import com.yuhtin.lauren.models.objects.Config;
import com.yuhtin.lauren.sql.connection.ConnectionInfo;
import com.yuhtin.lauren.sql.connection.SQLConnection;
import com.yuhtin.lauren.sql.connection.mysql.MySQLConnection;
import com.yuhtin.lauren.sql.connection.sqlite.SQLiteConnection;
import com.yuhtin.lauren.utils.helper.InfinityFiles;
import lombok.Data;

import javax.inject.Singleton;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.FileHandler;

@Data
@Singleton
public abstract class LaurenDAO implements Bot {

    private String botName;

    private java.util.logging.Logger logger;
    private Injector injector;
    private SQLConnection sqlConnection;
    private Config config;

    private String version;
    private long botStartTime;

    /**
     * Called on bot starting
     */
    public void onEnable() {
    }

    /**
     * Called on bot connect to discord
     */
    public void onReady() {

    }

    /**
     * Called on bot disabled (forced and natural)
     */
    public void onDisable() {
    }

    public void shutdown() {

        onDisable();

        logger.info("Successfully shutdown");
        System.exit(0);

    }

    @Override
    public boolean connectDiscord() {

        return true;

    }

    @Override
    public boolean setupLogger() {

        this.logger = java.util.logging.Logger.getLogger(botName);

        if (this.config.isLog()) {

            InfinityFiles infinityFiles = new InfinityFiles("log", "logs", ".log", ".zip");
            try {

                FileHandler file = new FileHandler(infinityFiles.getNextFile());
                file.setFormatter(new LogFormat());
                this.logger.addHandler(file);

            } catch (Exception exception) {

                exception.printStackTrace();
                return false;

            }

        }

        this.logger.info("Logger setup successfully");
        return true;

    }

    @Override
    public boolean setupConfig() {

        this.config = Config.loadConfig("config/config.json");

        if (this.config == null) {
            this.logger.severe("There was an error loading the config");
            return false;


        }

        return true;

    }

    @Override
    public boolean setupGuice() {

        try {

            this.injector = Guice.createInjector(new LaurenModule(this));
            this.injector.injectMembers(this);

        } catch (Exception exception) {

            exception.printStackTrace();
            return false;

        }

        return true;

    }

    @Override
    public boolean configureConnection() {

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

        if (!this.sqlConnection.configure(connectionInfo)) return false;

        this.logger.info("Connection to database successful");
        return true;

    }

    @Override
    public boolean findVersion(Class oClass) {

        Properties properties = new Properties();

        try {

            properties.load(oClass.getClassLoader().getResourceAsStream("project.properties"));
            this.version = properties.getProperty("version");

            return true;

        } catch (Exception exception) {

            this.logger.info("An exception was caught while searching for my client version");
            return false;

        }

    }
}
