package application;

import logger.Logger;
import logger.data.LoggerDataSource;
import manager.CommandStartup;
import manager.ListenersStartup;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import java.io.*;
import java.util.stream.Collectors;

public class Lauren {

    public static JDA bot;
    public static LoggerDataSource logger;
    public static long startTime;

    public static void main(String[] args) throws Exception {
        logger = new LoggerDataSource();
        Logger.log("Lauren is now registering logs").save();
        File file = new File("token.json");
        if (!file.exists()) {
            if (!file.createNewFile()) return;
            Logger.log("Coloque um token válido no bot.").save();
            return;
        }
        BufferedReader reader = new BufferedReader(new FileReader(file.getAbsolutePath()));
        String botToken = reader.lines().collect(Collectors.toList()).get(0);
        reader.close();
        if (botToken == null) {
            Logger.log("Coloque um token válido no arquivo 'token.json'").save();
            return;
        }
        bot = new JDABuilder(AccountType.BOT).setToken(botToken).build();
        bot.setAutoReconnect(true);
        new ListenersStartup(bot, "events", "MemberEvents");
        new CommandStartup(bot, "commands", "ServerInfoCommand", "ClearCommand", "PingCommand", "RegisterCommand", "InfoCommand");
        Logger.log("Lauren is now online").save();
        startTime = System.currentTimeMillis();
        System.gc();
    }
}
