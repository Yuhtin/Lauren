package com.yuhtin.lauren.events;

import com.yuhtin.lauren.Lauren;
import com.yuhtin.lauren.utils.TaskHelper;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
public class BotReadyEvent extends ListenerAdapter {

    private final Lauren lauren;

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        lauren.getLogger().info("Bot connected successfully");
        TaskHelper.runTaskLaterAsync(new TimerTask() {
            @Override
            public void run() {
                lauren.getLogger().info("Running onReady event");
                lauren.onReady();
            }
        }, 5, TimeUnit.SECONDS);
    }
}
