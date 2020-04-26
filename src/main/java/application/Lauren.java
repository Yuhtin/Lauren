package application;

import logger.Logger;
import logger.data.LoggerDataSource;
import manager.CommandStartup;
import manager.ListenersStartup;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

public class Lauren {

    public static JDA bot;
    public static LoggerDataSource logger;
    public static long startTime;

    public static void main(String[] args) throws Exception {
        logger = new LoggerDataSource();
        Logger.log("Lauren is now registering logs").save();
        bot = new JDABuilder(AccountType.BOT).setToken("NzAyNTE4NTI2NzUzMjQzMTU2.XqBNhA.JxhSeEzwfbCUk2VzN8pfgtxwurQ").build();
        bot.setAutoReconnect(true);
        new ListenersStartup(bot, "events", "MemberEvents");
        new CommandStartup(bot, "commands", "ServerInfoCommand", "ClearCommand", "PingCommand", "RegisterCommand", "InfoCommand");
        Logger.log("Lauren is now online").save();
        startTime = System.currentTimeMillis();
        System.gc();
    }
}
