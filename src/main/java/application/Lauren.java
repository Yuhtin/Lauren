package application;

import database.Data;
import database.Database;
import database.types.MySQL;
import database.types.SQLite;
import logger.Logger;
import logger.controller.LoggerController;
import manager.CommandStartup;
import manager.ListenersStartup;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import configuration.Config;

public class Lauren {

    public static JDA bot;
    public static Guild guild;
    public static long startTime;
    public static Config config;
    public static Database data;

    public static void main(String[] args) throws Exception {
        long loadStart = System.currentTimeMillis();
        config = Config.startup();
        if (config == null) {
            Logger.log("There was an error loading the config");
            return;
        }

        if (config.log)
            new LoggerController("log");

        if (!startDatabase()) return;

        bot = new JDABuilder(AccountType.BOT).setToken(config.token).setActivity(Activity.watching("my project on github.com/Yuhtin/Lauren")).setAutoReconnect(true).build();

        new ListenersStartup(bot, "events");
        new CommandStartup(bot, "commands");
        guild = bot.getGuildById(700673055982354472L);
        startTime = System.currentTimeMillis();
        Logger.log("Lauren is now online").save();
        Logger.log("It took me " + (startTime - loadStart) + " millis to load my systems");
        System.gc();
    }

    public static boolean startDatabase() {
        data = new Database(selectDatabase(config.databaseType), "lauren");

        if (data.isNull() || !data.createTable() || !data.loadData()) {
            Logger.log("Database initialization error occurred").save();
            Logger.log("Shutting down the system");
            return false;
        }

        Logger.log("Connection to database successful").save();
        return true;
    }

    private static Data selectDatabase(String databaseType) {
        if (databaseType.equalsIgnoreCase("MySQL"))
            return new MySQL(config.mySqlHost, config.mySqlUser, config.mySqlPassword, config.mySqlDatabase, 3306);
        return new SQLite();
    }
}