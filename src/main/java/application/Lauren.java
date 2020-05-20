package application;

import logger.Logger;
import logger.data.LoggerDataSource;
import manager.CommandStartup;
import manager.ListenersStartup;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.RichPresence;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.InterfacedEventManager;
import objects.configuration.Config;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Lauren {

    public static JDA bot;
    public static LoggerDataSource logger;
    public static long startTime;
    public static Config config;

    public static void main(String[] args) throws Exception {
        config = Config.startup();
        if (config == null) {
            Logger.log("Ocorreu um erro ao carregar a config");
            return;
        }
        if (!Lauren.config.log) {
            logger = new LoggerDataSource("log");
            Logger.log("Lauren is now registering logs").save();
        }

        bot = new JDABuilder(AccountType.BOT).setToken(config.token).setAutoReconnect(true).setActivity(Activity.watching("você batendo pra mim")).setBulkDeleteSplittingEnabled(false).setEventManager(new ThreadedEventManager()).build();

        new ListenersStartup(bot, "events", "MemberEvents", "registration.MemberReactionEvent");
        new CommandStartup(bot, "commands", "ServerInfoCommand", "ClearCommand", "AjudaCommand", "PingCommand", "RegisterCommand", "InfoCommand", "ConfigCommand");
        Logger.log("Lauren is now online").save();
        startTime = System.currentTimeMillis();
        System.gc();
    }

    private static class ThreadedEventManager extends InterfacedEventManager {
        private final ExecutorService threadPool;

        private ThreadedEventManager() {
            this.threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
        }

        @Override
        public void handle(@Nonnull GenericEvent event) {
            this.threadPool.submit(() -> super.handle(event));
        }
    }
}