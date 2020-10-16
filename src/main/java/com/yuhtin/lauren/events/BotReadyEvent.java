package com.yuhtin.lauren.events;

import com.yuhtin.lauren.Lauren;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.models.enums.LogType;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class BotReadyEvent extends ListenerAdapter {

    @Override
    public void onReady(ReadyEvent event) {
        Logger.log("Lauren has connected to DiscordAPI", LogType.STARTUP).save();

        if (Lauren.config.laurenTest) Lauren.guild = event.getJDA().getGuildById(723625569111113740L);
        else Lauren.guild = event.getJDA().getGuildCache().iterator().next();
    }
}
