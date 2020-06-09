package application;

import database.Data;
import database.types.MariaDB;
import database.types.MongoDB;
import database.types.MySQL;
import database.types.SQLite;
import logger.Logger;
import logger.data.LoggerDataSource;
import manager.CommandStartup;
import manager.ListenersStartup;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import objects.configuration.Config;

public class Lauren {

    public static JDA bot;
    public static LoggerDataSource logger;
    public static long startTime;
    public static Config config;
    public static Data data;

    public static void main(String[] args) throws Exception {
        config = Config.startup();
        if (config == null) {
            Logger.log("Ocorreu um erro ao carregar a config");
            return;
        }

        if (!config.log) {
            logger = new LoggerDataSource("log");
            Logger.log("Lauren is now registering logs").save();
        }

        data = selectDatabase(config.databaseType);
        if (!data.openConnection() || !data.loadData()) {
            Logger.log("Occorreu um erro na inicialização do banco de dados").save();
            Logger.log("Desligando o sistema");
            return;
        }

        bot = new JDABuilder(AccountType.BOT).setToken(config.token).setActivity(Activity.watching("my project on github.com/Yuhtin/Lauren")).setAutoReconnect(true).build();

        new ListenersStartup(bot, "events", "MemberEvents", "registration.MemberReactionEvent",
                "experience.ChatMessage");
        new CommandStartup(bot, "commands", "ServerInfoCommand", "ClearCommand", "AjudaCommand", "PingCommand",
                "RegisterCommand", "InfoCommand", "player.SetPointsCommand", "ConfigCommand", "AvatarCommand",
                "player.PlayerInfoCommand");
        Logger.log("Lauren is now online").save();
        startTime = System.currentTimeMillis();
        System.gc();
    }

    private static Data selectDatabase(String databaseType) {
        switch (databaseType) {
            case "SQLite":
                return new SQLite(null, "lauren_players");
            case "MySQL":
                return new MySQL(null, config.mySqlHost, config.mySqlUser, config.mySqlPassword, config.mySqlDatabase, "lauren_players", 3306);
            case "MongoDB":
                return new MongoDB(null, "lauren_players", config.mongoPassword);
            default:
                return new MariaDB();
        }
    }
}