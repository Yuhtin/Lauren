package com.yuhtin.lauren;

import com.google.inject.Inject;
import com.yuhtin.lauren.core.bot.LaurenDAO;
import com.yuhtin.lauren.core.music.TrackManager;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.core.statistics.StatsController;
import com.yuhtin.lauren.core.xp.XpController;
import com.yuhtin.lauren.models.embeds.ShopEmbed;
import com.yuhtin.lauren.manager.CommandManager;
import com.yuhtin.lauren.manager.EventsManager;
import com.yuhtin.lauren.manager.TimerManager;
import com.yuhtin.lauren.service.LocaleManager;
import com.yuhtin.lauren.service.PterodactylConnection;
import com.yuhtin.lauren.sql.dao.PlayerDAO;
import com.yuhtin.lauren.sql.dao.StatisticDAO;
import com.yuhtin.lauren.tasks.*;
import com.yuhtin.lauren.utils.helper.TaskHelper;
import com.yuhtin.lauren.utils.helper.Utilities;
import com.yuhtin.lauren.utils.messages.AsciiBox;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;
import java.util.zip.ZipOutputStream;

public class Lauren extends LaurenDAO {

    // DAO's
    @Inject private PlayerDAO playerDAO;
    @Inject private StatisticDAO statisticDAO;

    // Controllers
    @Inject private XpController xpController;
    @Inject private PlayerController playerController;
    @Inject private StatsController statsController;

    // Managers
    @Inject private LocaleManager localeManager;
    @Inject private TimerManager timerManager;

    // Others
    @Inject private PterodactylConnection pterodactylConnection;
    @Inject private TopXpUpdater topXpUpdater;
    @Inject private ShopEmbed shopEmbed;

    @Getter private Guild guild;

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

        this.findVersion();
        this.connectDiscord();

        loadSQLTables();

        loadCommands();
        loadEvents();
        loadTasks();

        this.timerManager.register("com.yuhtin.lauren.timers.impl");

        this.pterodactylConnection.load(this.getConfig().getPteroKey());
        this.localeManager.searchHost(this.getConfig().getGeoIpAcessKey());

        this.shopEmbed.build();

    }

    @Override
    public void onReady() {

        this.getLogger().info("[3/3] Lauren is now ready");

        this.getBot().addEventListener(this.getEventWaiter());
        this.guild = this.getBot().getGuildById(700673055982354472L);

        String[] botInfo = new String[]{
                "",
                this.getBotName() + " v" + this.getVersion(),
                "Author: Yuhtin#9147",
                "",
                "All systems has loaded",
                this.getBotName() + " is now online"
        };

        this.getLogger().info(new AsciiBox()
                .size(50)
                .borders("━", "┃")
                .corners("┏", "┓", "┗", "┛")
                .render(botInfo)
        );

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

        this.getLogger().info(this.getBotName() + " disabled");

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
        this.statisticDAO.findAllStats().forEach(this.statsController::insertStats);

    }

    private void loadTasks() {

        AutoSaveTask autoSaveTask = new AutoSaveTask(
                this.statsController,
                this.playerController,
                this.getBot(),
                this.getLogger()
        );

        TimerCheckerTask timerCheckerTask = new TimerCheckerTask(
                this.timerManager,
                this.getLogger()
        );

        LootGeneratorTask lootGeneratorTask = new LootGeneratorTask(
                this.playerController,
                this.getBot(),
                this.getEventWaiter(),
                this.getLogger()
        );

        ShardLootTask shardLootTask = new ShardLootTask(
                this.playerController,
                this.getBot(),
                this.getEventWaiter(),
                this.getLogger()
        );

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
        shardLootTask.startRunnable();

    }
}
