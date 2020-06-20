package com.yuhtin.lauren.application;

import com.yuhtin.lauren.database.Data;
import com.yuhtin.lauren.database.Database;
import com.yuhtin.lauren.database.types.MySQL;
import com.yuhtin.lauren.database.types.SQLite;
import com.yuhtin.lauren.core.entities.Config;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.logger.controller.LoggerController;
import com.yuhtin.lauren.manager.CommandManager;
import com.yuhtin.lauren.manager.EventsManager;
import com.yuhtin.lauren.utils.helper.Utilities;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;

import java.io.*;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
        if (config.log) new LoggerController("log");
        if (!startDatabase()) return;

        bot = new JDABuilder(AccountType.BOT)
                .setToken(config.token)
                .setActivity(Activity.watching("my project on github.com/Yuhtin/Lauren"))
                .setAutoReconnect(true)
                .build();

        new EventsManager(bot, "com.yuhtin.lauren.events");
        new CommandManager(bot, "com.yuhtin.lauren.commands");
        startTime = System.currentTimeMillis();
        Logger.log("Lauren is now online").save();
        Logger.log("It took me " + ((startTime - loadStart) / 1000) + " seconds to load my systems");
        System.gc();

        Scanner scanner = new Scanner(System.in);
        while (scanner.nextLine().equalsIgnoreCase("stop")) {
            finish();
        }
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
            Logger.log("Starting log at " + now.getHour() + "h " + now.getMinute() + "m " + now.getSecond() + "s").save();

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