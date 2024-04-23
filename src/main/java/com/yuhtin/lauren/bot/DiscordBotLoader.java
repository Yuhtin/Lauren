package com.yuhtin.lauren.bot;

import com.yuhtin.lauren.util.EnvWrapper;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DiscordBotLoader {

    public static void connect(DiscordBot bot) {
        bot.onLoad();

        String token = EnvWrapper.get("DISCORD_BOT_TOKEN");
        if (token == null) {
            throw new IllegalStateException("DISCORD_BOT_TOKEN is not set!");
        }

        List<GatewayIntent> enabledIntents = Arrays.asList(
                GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_PRESENCES,
                GatewayIntent.GUILD_MESSAGE_REACTIONS
        );

        JDABuilder.create(token, enabledIntents)
                .setAutoReconnect(true)
                .setEventManager(new AnnotatedEventManager())
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setChunkingFilter(ChunkingFilter.ALL)
                .enableIntents(Arrays.asList(GatewayIntent.values()))
                .enableCache(Arrays.asList(CacheFlag.values()))
                .setLargeThreshold(100)
                .addEventListeners(new BotConnectionListener(bot))
                .build();
    }

}
