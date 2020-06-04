package application;

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

    public static void main(String[] args) throws Exception {
        config = Config.startup();
        if (config == null) {
            Logger.log("Ocorreu um erro ao carregar a config");
            return;
        }

        if (!Lauren.config.log) {
            logger = new LoggerDataSource("log");
            Logger.log("Lauren is now registering logs").save();
        }

        bot = new JDABuilder(AccountType.BOT).setToken(config.token).setActivity(Activity.watching("my project on github.com/Yuhtin/Lauren")).setAutoReconnect(true).build();

        new ListenersStartup(bot, "events", "MemberEvents", "registration.MemberReactionEvent");
        new CommandStartup(bot, "commands", "ServerInfoCommand", "ClearCommand", "AjudaCommand", "PingCommand", "RegisterCommand", "InfoCommand", "ConfigCommand");
        Logger.log("Lauren is now online").save();
        startTime = System.currentTimeMillis();
        System.gc();
    }
}