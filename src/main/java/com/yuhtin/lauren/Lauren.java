package com.yuhtin.lauren;

import com.yuhtin.lauren.bot.DiscordBot;
import com.yuhtin.lauren.module.ModuleManager;
import com.yuhtin.lauren.util.EnvWrapper;
import com.yuhtin.lauren.util.EventWaiter;
import com.yuhtin.lauren.util.LoggerUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

import java.util.logging.Logger;

@Data
@RequiredArgsConstructor
public class Lauren implements DiscordBot {

    private final Logger logger = Logger.getLogger("Lauren");
    private final long startupTime = System.currentTimeMillis();
    private final EventWaiter eventWaiter = new EventWaiter();

    private JDA jda;
    private Guild guild;
    private boolean debugMode;

    @Override
    public void onLoad() {
        debugMode = EnvWrapper.isDebugMode();
        LoggerUtil.formatLogger(debugMode);

        findVersion();
    }

    @Override
    public void onReady() {
        guild = jda.getGuildById(EnvWrapper.get("DISCORD_GUILD_ID"));

        ModuleManager.load(this);
        jda.addEventListener(eventWaiter);

        logger.info("Bot ready and running!");
        logger.info("Logged in as @" + jda.getSelfUser().getName());
    }

    @Override
    public void onDisable() {
        logger.info("Stopping bot...");

        ModuleManager.unloadAll();

        logger.info("Goodbye, cruel world!");
        logger.info("Bot disabled!");
    }

    private void findVersion() {
        logger.info("Lauren version: " + getClass().getPackage().getImplementationVersion());
    }

    public String getVersion() {
        return getClass().getPackage().getImplementationVersion();
    }

}
