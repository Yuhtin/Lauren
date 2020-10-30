package com.yuhtin.lauren;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.yuhtin.lauren.commands.music.QueueCommand;
import com.yuhtin.lauren.commands.utility.SugestionCommand;
import com.yuhtin.lauren.core.entities.Config;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.logger.controller.LoggerController;
import com.yuhtin.lauren.core.music.TrackManager;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.core.statistics.controller.StatsDatabase;
import com.yuhtin.lauren.core.xp.XpController;
import com.yuhtin.lauren.database.Data;
import com.yuhtin.lauren.database.DatabaseController;
import com.yuhtin.lauren.database.types.MySQL;
import com.yuhtin.lauren.database.types.SQLite;
import com.yuhtin.lauren.models.enums.LogType;
import com.yuhtin.lauren.models.manager.CommandManager;
import com.yuhtin.lauren.models.manager.EventsManager;
import com.yuhtin.lauren.service.PterodactylConnection;
import com.yuhtin.lauren.tasks.LootGeneratorTask;
import com.yuhtin.lauren.tasks.TopXpUpdater;
import com.yuhtin.lauren.utils.helper.TaskHelper;
import com.yuhtin.lauren.utils.helper.Utilities;
import com.yuhtin.lauren.utils.messages.AsciiBox;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipOutputStream;

@lombok.Data
public class Lauren {

    @Getter
    private static Lauren instance = new Lauren();

    private JDA bot;
    private Guild guild;
    private long startTime;
    private Config config;
    private String version;

    public static void main(String[] args) throws InterruptedException {
        instance.setStartTime(System.currentTimeMillis());

        instance.setConfig(Config.startup());
        if (instance.getConfig() == null) {
            Logger.log("There was an error loading the config", LogType.ERROR);
            return;
        }

        if (instance.getConfig().log) {
            try {
                new LoggerController();
            } catch (Exception exception) {
                instance.getConfig().setLog(false);
                Logger.log("I founded an error on load LoggerController, logs turned off", LogType.ERROR);
            }
        }

        EventWaiter eventWaiter = new EventWaiter();
        Thread buildThread = new Thread(() -> {
            try {
                processDatabase(instance.getConfig().databaseType);
                Utilities.INSTANCE.foundVersion();
                instance.setBot(JDABuilder.createDefault(instance.getConfig().token)
                        .setActivity(Activity.watching("my project on github.com/Yuhtin/Lauren"))
                        .setAutoReconnect(true)
                        .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGE_REACTIONS)
                        .build());

            } catch (LoginException exception) {
                Logger.log("The bot token is wrong", LogType.ERROR).save();
            }
        });
        buildThread.start();
        buildThread.join();

        TaskHelper.runAsync(() -> {
            new EventsManager(instance.getBot(), "com.yuhtin.lauren.events");
            new CommandManager(instance.getBot(), "com.yuhtin.lauren.commands");
            new PterodactylConnection(instance.getConfig().pteroKey);
            new Thread(Lauren::loadTasks).start();

            instance.getBot().addEventListener(eventWaiter);
            QueueCommand.getBuilder().setEventWaiter(eventWaiter);
            SugestionCommand.setWaiter(eventWaiter);
            XpController.getInstance();
        });


        String[] loadNonFormated = new String[]{
                "",
                "Lauren v" + instance.getVersion(),
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

        TaskHelper.runTaskLater(new TimerTask() {
            @Override
            public void run() {
                if (instance.getGuild() == null) finish();
            }
        }, 10, TimeUnit.SECONDS);

        TaskHelper.runTaskTimerAsync(new TimerTask() {
            @Override
            public void run() {
                StatsDatabase.save();
                PlayerController.INSTANCE.savePlayers();
            }
        }, 5, 5, TimeUnit.MINUTES);

        TopXpUpdater.getInstance().startRunnable();

        LootGeneratorTask.getInstance().startRunnable();
        Lauren.getInstance().getBot().addEventListener(LootGeneratorTask.getInstance().getEventWaiter());

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

            Utilities.INSTANCE.writeToZip(file, zipFileOutput);
            Utilities.INSTANCE.cleanUp(Paths.get(file.getPath()));

            zipFileOutput.close();
            outputStream.close();

            DatabaseController.getDatabase().shutdown();

            Logger.log("Successfully compressed file", LogType.FINISH).save();
        } catch (Exception exception) {
            exception.printStackTrace();
            Logger.log("Can't compress a log file", LogType.WARN).save();
        }

        System.exit(0);
    }

    private static void processDatabase(String databaseType) {
        Data dataType = new SQLite();
        if (databaseType.equalsIgnoreCase("MySQL"))
            dataType = new MySQL(instance.getConfig().mySqlHost,
                    instance.getConfig().mySqlUser,
                    instance.getConfig().mySqlPassword,
                    instance.getConfig().mySqlDatabase);

        DatabaseController.get().constructDatabase(dataType.openConnection());
        DatabaseController.get().loadAll();

        Logger.log("Connection to database successful", LogType.STARTUP).save();
    }
}