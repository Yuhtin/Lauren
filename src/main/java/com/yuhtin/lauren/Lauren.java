package com.yuhtin.lauren;

import com.yuhtin.lauren.bot.DiscordBot;
import com.yuhtin.lauren.commands.CommandRegistry;
import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.guice.LaurenModule;
import com.yuhtin.lauren.manager.EventsManager;
import com.yuhtin.lauren.models.exceptions.GuiceInjectorException;
import com.yuhtin.lauren.tasks.AutoSaveTask;
import com.yuhtin.lauren.tasks.LootGeneratorTask;
import com.yuhtin.lauren.tasks.ShardLootTask;
import com.yuhtin.lauren.tasks.TimerCheckerTask;
import com.yuhtin.lauren.util.EnvWrapper;
import com.yuhtin.lauren.util.LoggerUtil;
import com.yuhtin.lauren.util.TaskHelper;
import lombok.Data;
import lombok.Getter;
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
