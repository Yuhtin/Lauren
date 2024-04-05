package com.yuhtin.lauren.bot;

import com.yuhtin.lauren.util.TaskHelper;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class BotConnectionListener {

    private final DiscordBot bot;

    @SubscribeEvent
    public void onReady(@NotNull ReadyEvent event) {
        bot.setJda(event.getJDA());
        TaskHelper.runAsync(bot::onReady);
    }

}