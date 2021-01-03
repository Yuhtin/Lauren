package com.yuhtin.lauren;

import com.google.inject.Inject;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.yuhtin.lauren.core.bot.LaurenDAO;
import com.yuhtin.lauren.core.music.TrackManager;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.core.statistics.StatsController;
import com.yuhtin.lauren.core.xp.XpController;
import com.yuhtin.lauren.models.manager.CommandManager;
import com.yuhtin.lauren.models.manager.EventsManager;
import com.yuhtin.lauren.models.manager.TimerManager;
import com.yuhtin.lauren.service.PterodactylConnection;
import com.yuhtin.lauren.sql.dao.ExperienceDAO;
import com.yuhtin.lauren.sql.dao.PlayerDAO;
import com.yuhtin.lauren.sql.dao.StatisticDAO;
import com.yuhtin.lauren.tasks.AutoSaveTask;
import com.yuhtin.lauren.tasks.LootGeneratorTask;
import com.yuhtin.lauren.tasks.TimerCheckerTask;
import com.yuhtin.lauren.tasks.TopXpUpdater;
import com.yuhtin.lauren.utils.helper.TaskHelper;
import com.yuhtin.lauren.utils.helper.Utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;
import java.util.zip.ZipOutputStream;

public class Lauren extends LaurenDAO {

    @Inject private PlayerDAO playerDAO;
    @Inject private StatisticDAO statisticDAO;

    @Inject private XpController xpController;
    @Inject private PlayerController playerController;
    @Inject private StatsController statsController;

    @Inject private TimerManager timerManager;
    @Inject private PterodactylConnection pterodactylConnection;

    @Inject private TopXpUpdater topXpUpdater;

    public Lauren(String botName) {
        this.setBotName(botName);
    }

    @Override
    public void onLoad() throws Exception {

        this.setBotStartTime(System.currentTimeMillis());

        this.setupConfig();
        this.setupLogger();

        this.configureConnection();
        this.setupGuice();

        this.getInjector().injectMembers(this);

    }

    @Override
    public void onEnable() throws Exception {

        this.findVersion(Lauren.class);
        this.connectDiscord();

        loadSQLTables();

        loadCommands();
        loadEvents();

        this.timerManager.register("com.yuhtin.lauren.timers.impl");
        this.pterodactylConnection.load(this.getConfig().getPteroKey());

    }

    @Override
    public void onReady() {

        this.getLogger().info("[3/3] Lauren is now ready");

    }

    @Override
    public void onDisable() throws Exception {

        if (!this.getSqlConnection().findConnection().isClosed()) {

            this.playerController.savePlayers();
            this.statsController.getStats().values().forEach(this.statisticDAO::updateStatistic);

            this.getLogger().info("Saved player's and statistic's data");

        } else {

            this.getLogger().warning("SQLConnection is closed, reconfiguring");
            this.configureConnection();

            this.getLogger().info("Executing onDisable again");
            onDisable();
            return;

        }

        TrackManager.getGuildTrackManagers().values().forEach(TrackManager::destroy);
        this.getLogger().info("Destroyed all track managers");

        this.getLogger().info("Lauren disabled");

        for (Handler handler : this.getLogger().getHandlers()) {

            handler.close();
            this.getLogger().removeHandler(handler);

        }

        File file = new File(this.getLogFile());

        FileOutputStream outputStream = new FileOutputStream(file.getPath().split("\\.")[0] + ".zip");
        ZipOutputStream zipFileOutput = new ZipOutputStream(outputStream);

        Utilities.INSTANCE.writeToZip(file, zipFileOutput);

        this.getLogger().info("Zipped last log file successfully");


    }

    @Override
    public void loadCommands() throws IOException {

        CommandManager commandManager = new CommandManager(
                this.getBot(),
                this.getInjector(),
                this.getLogger(),
                "com.yuhtin.lauren.commands"
        );

        commandManager.load();

    }

    @Override
    public void loadEvents() throws IOException {

        EventsManager eventsManager = new EventsManager(this.getBot(),
                this.getInjector(),
                this.getLogger(),
                "com.yuhtin.lauren.commands"
        );

        eventsManager.load();

    }

    private void loadSQLTables() {

        this.playerDAO.createTable();
        this.xpController.load();
        this.statisticDAO.createTable();

    }

    private void loadTasks() {

        EventWaiter eventWaiter = new EventWaiter();

        AutoSaveTask autoSaveTask = new AutoSaveTask(this.statsController, this.playerController, this.getLogger());
        TimerCheckerTask timerCheckerTask = new TimerCheckerTask(this.timerManager, this.getLogger());
        LootGeneratorTask lootGeneratorTask = new LootGeneratorTask(this.playerController, this.getLogger(), eventWaiter);

        TaskHelper.runTaskLater(new TimerTask() {
            @Override
            public void run() {
                if (!isLoaded()) shutdown();
            }
        }, 10, TimeUnit.SECONDS);

        TaskHelper.runTaskTimerAsync(autoSaveTask, 5, 5, TimeUnit.MINUTES);
        TaskHelper.runTaskTimerAsync(timerCheckerTask, 1, 1, TimeUnit.MINUTES);

        this.topXpUpdater.startRunnable();
        lootGeneratorTask.startRunnable();

    }
}
