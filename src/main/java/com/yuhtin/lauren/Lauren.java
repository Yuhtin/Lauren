package com.yuhtin.lauren;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.yuhtin.lauren.commands.CommandCatcher;
import com.yuhtin.lauren.core.bot.LaurenDAO;
import com.yuhtin.lauren.core.logger.controller.LoggerController;
import com.yuhtin.lauren.core.music.TrackManager;
import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.core.statistics.StatsController;
import com.yuhtin.lauren.core.xp.XpController;
import com.yuhtin.lauren.events.BotReadyEvent;
import com.yuhtin.lauren.guice.LaurenModule;
import com.yuhtin.lauren.manager.EventsManager;
import com.yuhtin.lauren.manager.TimerManager;
import com.yuhtin.lauren.models.embeds.ShopEmbed;
import com.yuhtin.lauren.models.enums.LogType;
import com.yuhtin.lauren.models.exceptions.GuiceInjectorException;
import com.yuhtin.lauren.service.LocaleManager;
import com.yuhtin.lauren.sql.dao.PlayerDAO;
import com.yuhtin.lauren.sql.dao.StatisticDAO;
import com.yuhtin.lauren.tasks.*;
import com.yuhtin.lauren.utils.helper.FileUtil;
import com.yuhtin.lauren.utils.helper.TaskHelper;
import lombok.Getter;
import lombok.val;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
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

@Getter
public final class Lauren extends LaurenDAO {

    // DAO's
    @Inject private PlayerDAO playerDAO;
    @Inject private StatisticDAO statisticDAO;

    // Controllers
    @Inject private XpController xpController;
    @Inject private PlayerController playerController;
    @Inject private StatsController statsController;
    @Inject private LoggerController loggerController;
    @Inject private CommandCatcher commandCatcher;

    // Managers
    @Inject private LocaleManager localeManager;
    @Inject private TimerManager timerManager;

    // Others
    @Inject private TopXpUpdater topXpUpdater;
    @Inject private ShopEmbed shopEmbed;

    @Getter private Guild guild;

    public Lauren(String botName) {
        setBotName(botName);
    }

    @Override
    public void onLoad() throws Exception {

        setBotStartTime(System.currentTimeMillis());
        setupConfig();

        configureConnection();

        findVersion();
        connectDiscord();

        setupGuice();
        getInjector().injectMembers(this);

        loggerController.create();

    }

    @Override
    public void onEnable() throws Exception {

        loadSQLTables();

        loadCommands();
        loadEvents();

        timerManager.register("com.yuhtin.lauren.timers.impl");
        localeManager.searchHost(getConfig().getGeoIpAccessKey());

        shopEmbed.build();

    }

    @Override
    public void onReady() {

        loadTasks();

        guild = getBot().getGuildById(700673055982354472L);

        Arrays.asList(
                "",
                getBotName() + " v" + getVersion(),
                "Author: Yuhtin#9147",
                "",
                "All systems has loaded",
                getBotName() + " is now online"
        ).forEach(System.out::println);

        getLogger().info("[2/3] Lauren is now online");

    }

    @Override
    public void onDisable() throws Exception {

        if (!getSqlConnection().findConnection().isClosed()) {

            playerController.savePlayers();
            statsController.getStats().values().forEach(statisticDAO::updateStatistic);

            getLogger().info("Saved player's and statistic's data");

        } else {

            getLogger().warning("SQLConnection is closed, reconfiguring");
            configureConnection();

            getLogger().info("Executing onDisable again");
            onDisable();
            return;

        }

        TrackManager.getGuildTrackManagers().values().forEach(TrackManager::destroy);
        getLogger().info("Destroyed all track managers");

        getLogger().info(getBotName() + " disabled");

        LocalDateTime now = LocalDateTime.now();
        File file = loggerController.getFile();

        getLogger().log("Compressing the log '" + file.getName() + "' to a zip file", LogType.FINISH);
        getLogger().log("Ending log at " + now.getHour() + "h " + now.getMinute() + "m " + now.getSecond() + "s", LogType.FINISH);

        FileOutputStream outputStream = new FileOutputStream(file.getPath().split("\\.")[0] + ".zip");
        ZipOutputStream zipFileOutput = new ZipOutputStream(outputStream);

        FileUtil.writeToZip(file, zipFileOutput);
        FileUtil.cleanUp(Paths.get(file.getPath()));

        zipFileOutput.close();
        outputStream.close();

        getLogger().info("Zipped last log file successfully");

    }

    @Override
    public void connectDiscord() throws LoginException {

        val jdaBuilder = JDABuilder.createDefault(getConfig().getToken())
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setChunkingFilter(ChunkingFilter.ALL)
                .enableIntents(Arrays.asList(GatewayIntent.values()))
                .setAutoReconnect(true)
                .addEventListeners(new BotReadyEvent(this));

        for (int i = 0; i < 10; i++) { jdaBuilder.useSharding(i, 10).build();}

        setBot(jdaBuilder.build());
    }

    @Override
    public void loadCommands() throws IOException {

        CommandManager commandManager = new CommandManager(
                getBot(),
                getInjector(),
                getLogger(),
                "com.yuhtin.lauren.commands"
        );

        commandManager.load();

    }

    @Override
    public void loadEvents() throws IOException {

        EventsManager eventsManager = new EventsManager(
                getBot(),
                getInjector(),
                getLogger(),
                "com.yuhtin.lauren.events"
        );

        eventsManager.load();

    }

    @Override
    public void setupGuice() throws GuiceInjectorException {

        try {

            setInjector(Guice.createInjector(new LaurenModule(this)));
            getInjector().injectMembers(this);
            getInjector().injectMembers(Player.class);

        } catch (Exception exception) {
            exception.printStackTrace();
            throw new GuiceInjectorException();
        }

    }

    private void loadSQLTables() {

        playerDAO.createTable();
        xpController.load();

        statisticDAO.createTable();
        statisticDAO.findAllStats().forEach(statsController::insertStats);

    }

    private void loadTasks() {

        AutoSaveTask autoSaveTask = new AutoSaveTask(
                statsController,
                playerController,
                getBot(),
                getLogger()
        );

        TimerCheckerTask timerCheckerTask = new TimerCheckerTask(
                timerManager,
                getLogger()
        );

        LootGeneratorTask lootGeneratorTask = new LootGeneratorTask(
                playerController,
                getBot(),
                getEventWaiter(),
                getLogger()
        );

        ShardLootTask shardLootTask = new ShardLootTask(
                playerController,
                getBot(),
                getEventWaiter(),
                getLogger()
        );

        TaskHelper.runTaskTimerAsync(autoSaveTask, 5, 5, TimeUnit.MINUTES);
        TaskHelper.runTaskTimerAsync(timerCheckerTask, 1, 1, TimeUnit.MINUTES);

        topXpUpdater.startRunnable();
        lootGeneratorTask.startRunnable();
        shardLootTask.startRunnable();

    }
}
