package com.yuhtin.lauren.application;

import com.yuhtin.lauren.core.entities.Config;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.logger.controller.LoggerController;
import com.yuhtin.lauren.database.Data;
import com.yuhtin.lauren.database.Database;
import com.yuhtin.lauren.database.types.MySQL;
import com.yuhtin.lauren.database.types.SQLite;
import com.yuhtin.lauren.manager.CommandManager;
import com.yuhtin.lauren.manager.EventsManager;
import com.yuhtin.lauren.utils.helper.Utilities;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.ZipOutputStream;

public class Lauren {

    public static JDA bot;
    public static Guild guild;
    public static long startTime;
    public static Config config;
    public static Database data;

    public static void main(String[] args) throws InterruptedException {
        config = Config.startup();
        if (config == null) {
            Logger.log("There was an error loading the config");
            return;
        }

        if (config.log) {
            try {
                new LoggerController("log");
            } catch (Exception exception) {
                config.setLog(false);
                Logger.log("I founded a error on load LoggerController, logs turned off");
            }
        }

        if (!startDatabase()) return;
        Thread buildThread = new Thread(() -> {
            try {
                bot = new JDABuilder(AccountType.BOT)
                        .setToken(config.token)
                        .setActivity(Activity.watching("my project on github.com/Yuhtin/Lauren"))
                        .setAutoReconnect(true)
                        .build();
                Logger.log("Lauren has connected to DiscordAPI").save();
            } catch (LoginException exception) {
                Logger.log("The bot token is wrong").save();
            }
        });
        buildThread.start();
        buildThread.join();

        new Thread(() -> {
            new EventsManager(bot, "com.yuhtin.lauren.events");
            new CommandManager(bot, "com.yuhtin.lauren.commands");
            loadGuild();
        }).start();


        Scanner scanner = new Scanner(System.in);
        while (scanner.nextLine().equalsIgnoreCase("stop")) {
            finish();
        }

        Logger.log("Lauren is now online").save();
    }

    private static void loadGuild() {
        /*
            Wait 2 seconds for the bot to connect completely before asking for a value
         */
        new Timer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        guild = bot.getGuildById(723625569111113740L);
                    }
                }, 2000
        );
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
            return new MySQL(config.mySqlHost, config.mySqlUser, config.mySqlPassword, config.mySqlDatabase);
        return new SQLite();
    }

    public static void finish() {
        try {
            LocalDateTime now = LocalDateTime.now();

            File file = LoggerController.get().getFile();
            Logger.log("Compressing the log '" + file.getName() + "' to a zip file").save();
            Logger.log("Ending log at " + now.getHour() + "h " + now.getMinute() + "m " + now.getSecond() + "s").save();

            FileOutputStream fos = new FileOutputStream(file.getPath().split("\\.")[0] + ".zip");
            ZipOutputStream zipOS = new ZipOutputStream(fos);

            Utilities.writeToZip(file, zipOS);
            if (!file.delete()) Logger.log("Can't delete a log file");
            zipOS.close();
            fos.close();
        } catch (Exception exception) {
            Logger.log("Can't compress a log file").save();
        }
        Lauren.config.updateConfig();
        System.exit(0);
    }
}