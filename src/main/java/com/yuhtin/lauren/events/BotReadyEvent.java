package com.yuhtin.lauren.events;

import com.google.inject.Inject;
import com.yuhtin.lauren.core.bot.LaurenDAO;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class BotReadyEvent extends ListenerAdapter {

    @Inject private LaurenDAO laurenDAO;

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        this.laurenDAO.onReady();
    }
}
