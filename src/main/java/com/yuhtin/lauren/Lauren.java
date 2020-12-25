package com.yuhtin.lauren;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.yuhtin.lauren.commands.music.QueueCommand;
import com.yuhtin.lauren.commands.utility.ShopCommand;
import com.yuhtin.lauren.commands.utility.SugestionCommand;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.logger.controller.LoggerController;
import com.yuhtin.lauren.core.music.TrackManager;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.core.statistics.controller.StatsDatabase;
import com.yuhtin.lauren.core.xp.XpController;
import com.yuhtin.lauren.database.DatabaseController;
import com.yuhtin.lauren.guice.LaurenModule;
import com.yuhtin.lauren.models.embeds.ShopEmbed;
import com.yuhtin.lauren.models.enums.LogType;
import com.yuhtin.lauren.models.manager.CommandManager;
import com.yuhtin.lauren.models.manager.EventsManager;
import com.yuhtin.lauren.models.manager.TimerManager;
import com.yuhtin.lauren.models.objects.Config;
import com.yuhtin.lauren.service.LocaleManager;
import com.yuhtin.lauren.service.PterodactylConnection;
import com.yuhtin.lauren.sql.connection.ConnectionInfo;
import com.yuhtin.lauren.sql.connection.SQLConnection;
import com.yuhtin.lauren.sql.connection.mysql.MySQLConnection;
import com.yuhtin.lauren.sql.connection.sqlite.SQLiteConnection;
import com.yuhtin.lauren.tasks.*;
import com.yuhtin.lauren.utils.helper.TaskHelper;
import com.yuhtin.lauren.utils.helper.Utilities;
import com.yuhtin.lauren.utils.messages.AsciiBox;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Scanner;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipOutputStream;

@lombok.Data
public class Lauren {

    @Getter
    private static Lauren instance = new Lauren();
    private Injector injector;

    private SQLConnection sqlConnection;
    private ShardManager bot;
    private Guild guild;
    private long startTime;
    private Config config;
    private String version;

    public static void main(String[] args) throws InterruptedException {
        instance.setStartTime(System.currentTimeMillis());

        instance.injector = Guice.createInjector(new LaurenModule(instance));

        instance.setConfig(Config.startup());
        if (instance.getConfig() == null) {
            Logger.log("There was an error loading the config", LogType.ERROR);
            return;
        }

        if (instance.getConfig().isLog()) {
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
                Utilities.INSTANCE.foundVersion();
                processDatabase(instance.getConfig().getDatabaseType());
                instance.setBot(DefaultShardManagerBuilder.create(instance.getConfig().getToken(), Arrays.asList(GatewayIntent.values()))
                        .setAutoReconnect(true)
                        .build());

            } catch (LoginException exception) {
                Logger.log("The bot token is wrong", LogType.ERROR);
            }
        });

        buildThread.start();
        buildThread.join();

        TaskHelper.runAsync(() -> {

            new EventsManager(instance.getBot(), "com.yuhtin.lauren.events");
            new CommandManager(instance.getBot(), "com.yuhtin.lauren.commands");

            TimerManager timerManager = new TimerManager("com.yuhtin.lauren.timers.impl");
            timerManager.register();

            new PterodactylConnection(instance.getConfig().getPteroKey());
            new Thread(Lauren::loadTasks).start();

            LocaleManager.getInstance().searchHost(instance.getConfig().getGeoIpAcessKey());

            instance.getBot().addEventListener(eventWaiter);
            instance.getBot().addEventListener(ShopCommand.getEventWaiter());
            QueueCommand.getBuilder().setEventWaiter(eventWaiter);
            SugestionCommand.setWaiter(eventWaiter);
            LootGeneratorTask.getInstance().setEventWaiter(eventWaiter);
            ShardLootTask.getInstance().setEventWaiter(eventWaiter);

            XpController.getInstance();
            ShopEmbed.getInstance().build();
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

        TaskHelper.runTaskTimerAsync(new PunishmentCheckerTask(), 5, 5, TimeUnit.MINUTES);

        TimerCheckerTask timerCheckerTask = new TimerCheckerTask();

        TaskHelper.runTaskTimerAsync(timerCheckerTask, 1, 1, TimeUnit.MINUTES);

        TopXpUpdater.getInstance().startRunnable();
        LootGeneratorTask.getInstance().startRunnable();
        ShardLootTask.getInstance().startRunnable();

    }

    public static void finish() {

        if (DatabaseController.getDatabase() != null) {

            PlayerController.INSTANCE.savePlayers();
            StatsDatabase.save();

        }

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
            Logger.log("Compressing the log '" + file.getName() + "' to a zip file", LogType.FINISH);
            Logger.log("Ending log at " + now.getHour() + "h " + now.getMinute() + "m " + now.getSecond() + "s", LogType.FINISH);

            FileOutputStream outputStream = new FileOutputStream(file.getPath().split("\\.")[0] + ".zip");
            ZipOutputStream zipFileOutput = new ZipOutputStream(outputStream);

            Utilities.INSTANCE.writeToZip(file, zipFileOutput);
            Utilities.INSTANCE.cleanUp(Paths.get(file.getPath()));

            zipFileOutput.close();
            outputStream.close();

            DatabaseController.getDatabase().shutdown();

            Logger.log("Successfully compressed file", LogType.FINISH);
        } catch (Exception exception) {
            exception.printStackTrace();
            Logger.log("Can't compress a log file", LogType.WARN);
        }

        System.exit(0);
    }

    private static void processDatabase(String databaseType) {

        ConnectionInfo connectionInfo = ConnectionInfo.builder().build();

        if (databaseType.equalsIgnoreCase("MySQL")) instance.setSqlConnection(new MySQLConnection());
        else instance.setSqlConnection(new SQLiteConnection());


        /*DatabaseController.get().constructDatabase(connection);
        DatabaseController.get().loadAll();*/

        Logger.log("Connection to database successful", LogType.STARTUP);
    }
}