package com.yuhtin.lauren.events;

import com.yuhtin.lauren.module.Module;
import com.yuhtin.lauren.module.impl.player.module.PlayerModule;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ChatMessage extends ListenerAdapter {

    /* Earn 3 XP for every message sent */
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getChannelType().isGuild()
                || event.getMember() == null
                || event.getAuthor().isBot())
            return;

        Module.instance(PlayerModule.class)
                .retrieve(event.getMember().getIdLong())
                .thenAccept(player -> player.gainXP(3));
    }


}