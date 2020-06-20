package com.yuhtin.lauren.application;

import com.yuhtin.lauren.database.Data;
import com.yuhtin.lauren.database.Database;
import com.yuhtin.lauren.database.types.MySQL;
import com.yuhtin.lauren.database.types.SQLite;
import com.yuhtin.lauren.core.entities.Config;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.logger.controller.LoggerController;
import com.yuhtin.lauren.manager.CommandStartup;
import com.yuhtin.lauren.manager.ListenersStartup;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;

import java.io.*;
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

        new ListenersStartup(bot, "com.yuhtin.lauren.events");
        new CommandStartup(bot, "com.yuhtin.lauren.commands");
        startTime = System.currentTimeMillis();
        Logger.log("Lauren is now online").save();
        Logger.log("It took me " + (startTime - loadStart) + " millis to load my systems");
        System.gc();

        Scanner scanner = new Scanner(System.in);
        while (scanner.nextLine().equalsIgnoreCase("stop")) {
            try {
                FileOutputStream fos = new FileOutputStream(LoggerController.get().getFile().getPath().split("\\.")[0] + ".zip");
                ZipOutputStream zipOS = new ZipOutputStream(fos);

                writeToZip(LoggerController.get().getFile(), zipOS);
                if (!LoggerController.get().getFile().delete()) Logger.log("Can't delete a log file");
                zipOS.close();
                fos.close();
            } catch (Exception exception) {
                Logger.log("Can't compress a log file").save();
            }
            System.exit(0);
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

    public static void writeToZip(File file, ZipOutputStream zipStream) throws IOException {
        Logger.log("Compressing the log '" + file.getName() + "' to a zip file");
        FileInputStream fis = new FileInputStream(file);
        ZipEntry zipEntry = new ZipEntry(file.getName());

        zipStream.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipStream.write(bytes, 0, length);
        }

        zipStream.closeEntry();
        fis.close();
    }
}