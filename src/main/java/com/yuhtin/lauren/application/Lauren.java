package com.yuhtin.lauren.application;

import com.yuhtin.lauren.core.entities.Config;
import com.yuhtin.lauren.core.logger.LogType;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.logger.controller.LoggerController;
import com.yuhtin.lauren.core.match.controller.MatchController;
import com.yuhtin.lauren.database.Data;
import com.yuhtin.lauren.database.Database;
import com.yuhtin.lauren.database.types.MySQL;
import com.yuhtin.lauren.database.types.SQLite;
import com.yuhtin.lauren.manager.CommandManager;
import com.yuhtin.lauren.manager.EventsManager;
import com.yuhtin.lauren.utils.helper.TaskHelper;
import com.yuhtin.lauren.utils.helper.Utilities;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
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
            Logger.log("There was an error loading the config", LogType.ERROR);
            return;
        }

        if (config.log) {
            try {
                new LoggerController("log");
            } catch (Exception exception) {
                config.setLog(false);
                Logger.log("I founded a error on load LoggerController, logs turned off", LogType.ERROR);
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
                Logger.log("Lauren has connected to DiscordAPI", LogType.STARTUP).save();
            } catch (LoginException exception) {
                Logger.log("The bot token is wrong", LogType.ERROR).save();
            }
        });
        buildThread.start();
        buildThread.join();

        new Thread(() -> {
            new EventsManager(bot, "com.yuhtin.lauren.events");
            new CommandManager(bot, "com.yuhtin.lauren.commands");
            MatchController.startup();
            new Thread(Lauren::loadTasks).start();
        }).start();


        Scanner scanner = new Scanner(System.in);
        while (scanner.nextLine().equalsIgnoreCase("stop")) {
            new Thread(Lauren::finish).start();
        }

        Logger.log("Lauren is now online", LogType.STARTUP).save();
    }

    private static void loadTasks() {
        /*
            Wait 2 seconds for the bot to connect completely before asking for a value
         */
        TaskHelper.schedule(new TimerTask() {
            @Override
            public void run() {
                guild = bot.getGuildCache().iterator().next();
            }
        }, 4, TimeUnit.SECONDS);

        new Thread(() -> TaskHelper.timer(new TimerTask() {
            @Override
            public void run() {
                MatchController.findMatch();
            }
        }, 1, 1, TimeUnit.MINUTES)).start();
    }

    public static boolean startDatabase() {
        data = new Database(selectDatabase(config.databaseType), "lauren");

        if (data.isNull() || !data.createTable() || !data.loadData()) {
            Logger.log("Database initialization error occurred", LogType.ERROR).save();
            Logger.log("Shutting down the system", LogType.ERROR);
            return false;
        }

        Logger.log("Connection to database successful", LogType.STARTUP).save();
        return true;
    }

    private static Data selectDatabase(String databaseType) {
        if (databaseType.equalsIgnoreCase("MySQL"))
            return new MySQL(config.mySqlHost, config.mySqlUser, config.mySqlPassword, config.mySqlDatabase);
        return new SQLite();
    }

    public static void finish() {
        try {
            data.close();
            LocalDateTime now = LocalDateTime.now();

            File file = LoggerController.get().getFile();
            Logger.log("Compressing the log '" + file.getName() + "' to a zip file", LogType.LOG).save();
            Logger.log("Ending log at " + now.getHour() + "h " + now.getMinute() + "m " + now.getSecond() + "s", LogType.LOG).save();

            FileOutputStream fos = new FileOutputStream(file.getPath().split("\\.")[0] + ".zip");
            ZipOutputStream zipOS = new ZipOutputStream(fos);

            new Thread(() -> {
                try {
                    Utilities.writeToZip(file, zipOS);
                } catch (IOException exception) {
                    Logger.log("Can't write log file to zip file", LogType.ERROR).save();
                }
            }).start();
            if (!file.delete()) Logger.log("Can't delete a log file", LogType.WARN).save();
            zipOS.close();
            fos.close();

            Logger.log("Successfully compressed file", LogType.LOG).save();
        } catch (Exception exception) {
            Logger.log("Can't compress a log file", LogType.ERROR).save();
        }
        Lauren.config.updateConfig();
        System.exit(0);
    }
}