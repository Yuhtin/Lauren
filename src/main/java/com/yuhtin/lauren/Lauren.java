package com.yuhtin.lauren;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.yuhtin.lauren.bot.DiscordBot;
import com.yuhtin.lauren.commands.CommandCatcher;
import com.yuhtin.lauren.commands.CommandRegistry;
import com.yuhtin.lauren.core.bot.LaurenDAO;
import com.yuhtin.lauren.core.logger.Logger;
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
import com.yuhtin.lauren.util.EnvWrapper;
import com.yuhtin.lauren.util.FileUtil;
import com.yuhtin.lauren.util.LoggerUtil;
import com.yuhtin.lauren.util.TaskHelper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import javax.security.auth.login.LoginException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.zip.ZipOutputStream;

@Getter
@RequiredArgsConstructor
public class Lauren implements DiscordBot {

    private final Logger logger = Logger.getLogger("Lauren");
    private final long startupTime = System.currentTimeMillis();

    private JDA jda;
    private Guild guild;
    private boolean debugMode;

    @Override
    public void onLoad() {
        debugMode = EnvWrapper.isDebugMode();
        LoggerUtil.formatLogger(debugMode);

        setupConfig();

        configureConnection();

        findVersion();
        setupGuice();

        connectDiscord();

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
        
        getLogger().info("[2/3] Lauren is now online");
    }

    @Override
    public void onDisable() {
        logger.info("Stopping bot...");

        ModuleManager.unloadAll();

        logger.info("Goodbye, cruel world!");
        logger.info("Bot disabled!");
    }

    @Override
    public void loadCommands() {
        val commandRegistry = CommandRegistry.of(getBot(), getInjector());
        getInjector().injectMembers(commandRegistry);

        commandRegistry.register();
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

            getLogger().info("Setuped Guice and injeted all classes");
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
