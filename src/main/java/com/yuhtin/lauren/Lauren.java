package com.yuhtin.lauren;

import com.yuhtin.lauren.bot.DiscordBot;
import com.yuhtin.lauren.commands.CommandRegistry;
import com.yuhtin.lauren.models.exceptions.GuiceInjectorException;
import com.yuhtin.lauren.tasks.AutoSaveTask;
import com.yuhtin.lauren.tasks.LootGeneratorTask;
import com.yuhtin.lauren.tasks.ShardLootTask;
import com.yuhtin.lauren.tasks.TimerCheckerTask;
import com.yuhtin.lauren.util.EnvWrapper;
import com.yuhtin.lauren.util.LoggerUtil;
import com.yuhtin.lauren.util.TaskHelper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Data
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
    public void onReady() {
        loadSQLTables();

        loadCommands();
        loadEvents();

        timerManager.register("com.yuhtin.lauren.timers.impl");
        localeManager.searchHost(getConfig().getGeoIpAccessKey());

        shopEmbed.build();
    }

    @Override
    public void onDisable() {
        logger.info("Stopping bot...");

        ModuleManager.unloadAll();

        logger.info("Goodbye, cruel world!");
        logger.info("Bot disabled!");
    }

    public void loadCommands() {
        val commandRegistry = CommandRegistry.of(getBot(), getInjector());
        getInjector().injectMembers(commandRegistry);

        commandRegistry.register();
    }

    public void loadEvents() throws IOException {

        EventsManager eventsManager = new EventsManager(
                getBot(),
                getInjector(),
                getLogger(),
                "com.yuhtin.lauren.events"
        );

        eventsManager.load();

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
