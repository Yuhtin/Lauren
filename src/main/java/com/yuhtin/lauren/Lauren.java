package com.yuhtin.lauren;

import com.yuhtin.lauren.core.alarm.controller.AlarmDatabase;
import com.yuhtin.lauren.core.entities.Config;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.logger.controller.LoggerController;
import com.yuhtin.lauren.core.match.controller.MatchController;
import com.yuhtin.lauren.core.match.controller.MatchDatabase;
import com.yuhtin.lauren.core.music.TrackManager;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.core.player.controller.PlayerDatabase;
import com.yuhtin.lauren.core.statistics.controller.StatsDatabase;
import com.yuhtin.lauren.database.Data;
import com.yuhtin.lauren.database.DatabaseController;
import com.yuhtin.lauren.database.types.MySQL;
import com.yuhtin.lauren.database.types.SQLite;
import com.yuhtin.lauren.models.enums.LogType;
import com.yuhtin.lauren.models.manager.CommandManager;
import com.yuhtin.lauren.models.manager.EventsManager;
import com.yuhtin.lauren.service.PterodactylConnection;
import com.yuhtin.lauren.utils.helper.TaskHelper;
import com.yuhtin.lauren.utils.helper.Utilities;
import com.yuhtin.lauren.utils.messages.AsciiBox;
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
    public static String version;

    public static void main(String[] args) throws InterruptedException {
        startTime = System.currentTimeMillis();

        config = Config.startup();
        if (config == null) {
            Logger.log("There was an error loading the config", LogType.ERROR);
            return;
        }

        if (config.log) {
            try {
                new LoggerController();
            } catch (Exception exception) {
                config.setLog(false);
                Logger.log("I founded a error on load LoggerController, logs turned off", LogType.ERROR);
            }
        }

        Thread buildThread = new Thread(() -> {
            try {
                processDatabase(config.databaseType);
                Utilities.INSTANCE.foundVersion();
                TrackManager.constructFields();
                bot = JDABuilder.createDefault(config.token)
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

        TaskHelper.runAsync(() -> {
            new EventsManager(bot, "com.yuhtin.lauren.events");
            new CommandManager(bot, "com.yuhtin.lauren.commands");
            new PterodactylConnection(config.pteroKey);
            MatchController.startup();
            new Thread(Lauren::loadTasks).start();
        });


        String[] loadNonFormated = new String[]{
                "",
                "Lauren v" + version,
                "Author: Yuhtin#9147",
                "",
                "All systems has loaded",
                "Lauren is now online"
        };

        Logger.log(new AsciiBox()
                .size(50)
                .borders("━", "┃")
                .corners("┏", "┓", "┗", "┛")
                .render(loadNonFormated), LogType.STARTUP);

        Scanner scanner = new Scanner(System.in);
        while (scanner.nextLine().equalsIgnoreCase("stop")) {
            new Thread(Lauren::finish).start();
        }
    }

    private static void loadTasks() {
        /* Wait 7 seconds for the bot to connect completely before asking for a value */

        TaskHelper.runTaskLater(new TimerTask() {
            @Override
            public void run() {
                if (config.laurenTest) guild = bot.getGuildById(723625569111113740L);
                else guild = bot.getGuildCache().iterator().next();
            }
        }, 7, TimeUnit.SECONDS);

        TaskHelper.runTaskTimerAsync(new TimerTask() {
            @Override
            public void run() {
                StatsDatabase.save();
                PlayerController.INSTANCE.savePlayers();
            }
        }, 5, 5, TimeUnit.MINUTES);
    }

    public static void finish() {
        PlayerController.INSTANCE.savePlayers();
        StatsDatabase.save();
        TrackManager.get().destroy();
        TaskHelper.runTaskLater(new TimerTask() {
            @Override
            public void run() {
                saveLog();
            }
        }, 6, TimeUnit.SECONDS);
    }

    private static void saveLog() {

        try {
            LocalDateTime now = LocalDateTime.now();

            File file = LoggerController.get().getFile();
            Logger.log("Compressing the log '" + file.getName() + "' to a zip file", LogType.FINISH).save();
            Logger.log("Ending log at " + now.getHour() + "h " + now.getMinute() + "m " + now.getSecond() + "s", LogType.FINISH).save();

            FileOutputStream outputStream = new FileOutputStream(file.getPath().split("\\.")[0] + ".zip");
            ZipOutputStream zipFileOutput = new ZipOutputStream(outputStream);

            try {
                Utilities.INSTANCE.writeToZip(file, zipFileOutput);
            } catch (IOException exception) {
                Logger.log("Can't write log file to zip file", LogType.ERROR).save();
            }

            if (!file.delete()) Logger.log("Can't delete a log file", LogType.WARN).save();
            zipFileOutput.close();
            outputStream.close();

            DatabaseController.getDatabase().shutdown();

            Logger.log("Successfully compressed file", LogType.FINISH).save();
        } catch (Exception exception) {
            exception.printStackTrace();
            Logger.log("Can't compress a log file", LogType.WARN).save();
        }

        Lauren.config.updateConfig();
        System.exit(0);
    }

    private static void processDatabase(String databaseType) {
        Data dataType = new SQLite();
        if (databaseType.equalsIgnoreCase("MySQL"))
            dataType = new MySQL(config.mySqlHost,
                    config.mySqlUser,
                    config.mySqlPassword,
                    config.mySqlDatabase);

        DatabaseController.get().constructDatabase(dataType.openConnection());

        PlayerDatabase.createTable();
        MatchDatabase.createTable();
        MatchDatabase.loadData();
        AlarmDatabase.createTable();
        AlarmDatabase.load();
        StatsDatabase.createTable();
        StatsDatabase.load();

        Logger.log("Connection to database successful", LogType.STARTUP).save();
    }
}