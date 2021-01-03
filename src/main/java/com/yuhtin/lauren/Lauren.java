package com.yuhtin.lauren;

import com.google.inject.Inject;
import com.yuhtin.lauren.core.bot.LaurenDAO;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.core.statistics.StatsController;
import com.yuhtin.lauren.models.manager.CommandManager;
import com.yuhtin.lauren.models.manager.EventsManager;
import com.yuhtin.lauren.models.manager.TimerManager;
import com.yuhtin.lauren.service.PterodactylConnection;
import com.yuhtin.lauren.sql.dao.ExperienceDAO;
import com.yuhtin.lauren.sql.dao.PlayerDAO;
import com.yuhtin.lauren.sql.dao.StatisticDAO;
import com.yuhtin.lauren.tasks.AutoSaveTask;
import com.yuhtin.lauren.tasks.TimerCheckerTask;
import com.yuhtin.lauren.utils.helper.TaskHelper;

import java.io.IOException;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class Lauren extends LaurenDAO {

    @Inject private PlayerDAO playerDAO;
    @Inject private ExperienceDAO experienceDAO;
    @Inject private StatisticDAO statisticDAO;

    @Inject private PlayerController playerController;
    @Inject private StatsController statsController;

    @Inject private TimerManager timerManager;
    @Inject private PterodactylConnection pterodactylConnection;

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
    public void onDisable() {

        this.getLogger().info("Lauren disabled");

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
        this.experienceDAO.createTable();
        this.statisticDAO.createTable();

    }

    private void loadTasks() {

        AutoSaveTask autoSaveTask = new AutoSaveTask(this.statsController, this.playerController, this.getLogger());
        TimerCheckerTask timerCheckerTask = new TimerCheckerTask(this.timerManager, this.getLogger());

        TaskHelper.runTaskLater(new TimerTask() {
            @Override
            public void run() {
                if (!isLoaded()) shutdown();
            }
        }, 10, TimeUnit.SECONDS);

        TaskHelper.runTaskTimerAsync(autoSaveTask, 5, 5, TimeUnit.MINUTES);
        TaskHelper.runTaskTimerAsync(timerCheckerTask, 1, 1, TimeUnit.MINUTES);

    }
}
