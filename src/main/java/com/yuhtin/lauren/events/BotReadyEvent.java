package com.yuhtin.lauren.events;

import com.yuhtin.lauren.Lauren;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class BotReadyEvent extends ListenerAdapter {

    private final Lauren lauren;

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        this.lauren.onReady();
    }
}
