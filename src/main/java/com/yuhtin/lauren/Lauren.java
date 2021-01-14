package com.yuhtin.lauren;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.yuhtin.lauren.core.bot.LaurenDAO;
import com.yuhtin.lauren.core.logger.controller.LoggerController;
import com.yuhtin.lauren.core.music.TrackManager;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.core.statistics.StatsController;
import com.yuhtin.lauren.core.xp.XpController;
import com.yuhtin.lauren.events.BotReadyEvent;
import com.yuhtin.lauren.guice.LaurenModule;
import com.yuhtin.lauren.manager.CommandManager;
import com.yuhtin.lauren.manager.EventsManager;
import com.yuhtin.lauren.manager.TimerManager;
import com.yuhtin.lauren.models.embeds.ShopEmbed;
import com.yuhtin.lauren.models.enums.LogType;
import com.yuhtin.lauren.models.exceptions.GuiceInjectorException;
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
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipOutputStream;

public class Lauren extends LaurenDAO {

    // DAO's
    @Inject private PlayerDAO playerDAO;
    @Inject private StatisticDAO statisticDAO;

    // Controllers
    @Inject private XpController xpController;
    @Inject private PlayerController playerController;
    @Inject private StatsController statsController;
    @Inject private LoggerController loggerController;

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

        this.configureConnection();

        this.findVersion();
        this.connectDiscord();

        this.setupGuice();
        this.getInjector().injectMembers(this);

        this.loggerController.create();

    }

    @Override
    public void onEnable() throws Exception {

        loadSQLTables();

        loadCommands();
        loadEvents();

        this.timerManager.register("com.yuhtin.lauren.timers.impl");

        this.pterodactylConnection.load(this.getConfig().getPteroKey());
        this.localeManager.searchHost(this.getConfig().getGeoIpAcessKey());

        this.shopEmbed.build();

    }

    @Override
    public void onReady() {

        loadTasks();

        this.guild = this.getBot().getGuildById(700673055982354472L);
        this.getBot().addEventListener(this.getEventWaiter());

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

        this.getLogger().info(this.getBotName() + " disabled");

        LocalDateTime now = LocalDateTime.now();
        File file = this.loggerController.getFile();

        this.getLogger().log("Compressing the log '" + file.getName() + "' to a zip file", LogType.FINISH);
        this.getLogger().log("Ending log at " + now.getHour() + "h " + now.getMinute() + "m " + now.getSecond() + "s", LogType.FINISH);

        FileOutputStream outputStream = new FileOutputStream(file.getPath().split("\\.")[0] + ".zip");
        ZipOutputStream zipFileOutput = new ZipOutputStream(outputStream);

        Utilities.INSTANCE.writeToZip(file, zipFileOutput);
        Utilities.INSTANCE.cleanUp(Paths.get(file.getPath()));

        zipFileOutput.close();
        outputStream.close();

        this.getLogger().info("Zipped last log file successfully");

    }

    @Override
    public void connectDiscord() throws LoginException {

        this.setBot(DefaultShardManagerBuilder.createDefault(this.getConfig().getToken())
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setChunkingFilter(ChunkingFilter.ALL)
                .enableIntents(Arrays.asList(GatewayIntent.values()))
                .setAutoReconnect(true)
                .addEventListeners(new BotReadyEvent(this))
                .build()
        );

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
                "com.yuhtin.lauren.events"
        );

        eventsManager.load();

    }

    @Override
    public void setupGuice() throws GuiceInjectorException {

        try {

            this.setInjector(Guice.createInjector(new LaurenModule(this)));
            this.getInjector().injectMembers(this);
            this.getInjector().injectMembers(Utilities.INSTANCE);

        } catch (Exception exception) {
            exception.printStackTrace();
            throw new GuiceInjectorException();
        }

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

        TaskHelper.runTaskTimerAsync(autoSaveTask, 5, 5, TimeUnit.MINUTES);
        TaskHelper.runTaskTimerAsync(timerCheckerTask, 1, 1, TimeUnit.MINUTES);

        this.topXpUpdater.startRunnable();
        lootGeneratorTask.startRunnable();
        shardLootTask.startRunnable();

    }
}
