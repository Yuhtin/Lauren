package com.yuhtin.lauren.events;

import com.yuhtin.lauren.LaurenStartup;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.models.enums.LogType;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class BotReadyEvent extends ListenerAdapter {

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        Logger.log("Lauren has connected to DiscordAPI", LogType.STARTUP);

        if (LaurenStartup.getInstance().getConfig().isLaurenTest()) LaurenStartup.getInstance().setGuild(event.getJDA().getGuildById(723625569111113740L));
        else LaurenStartup.getInstance().setGuild(event.getJDA().getGuildCache().iterator().next());
    }
}
