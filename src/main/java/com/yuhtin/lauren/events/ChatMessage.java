package com.yuhtin.lauren.events;

import com.yuhtin.lauren.core.player.controller.PlayerDataController;
import com.yuhtin.lauren.models.cache.CommandCache;
import com.yuhtin.lauren.utils.helper.LevenshteinCalculator;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ChatMessage extends ListenerAdapter {

    /* Earn 3 XP for every message sent */
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getChannelType().isGuild() || event.getMember() == null ||
                event.getAuthor().isBot())
            return;

        if (event.getMessage().getContentRaw().startsWith("$")) {
            String command = event.getMessage().getContentRaw().split(" ")[0].replace("$", "");
            if (!CommandCache.aliases.contains(command)) {
                for (String alias : CommandCache.aliases) {
                    if (LevenshteinCalculator.eval(command, alias) < 6) {
                        event.getChannel().sendMessage("<:chorano:726207542413230142> Esse comandinho não existe porém encontrei um parecido: `$" + alias + "`").queue();
                        break;
                    }
                }
            }
        }

        PlayerDataController.get(event.getMember().getIdLong()).gainXP(3).updateLevel().save();
    }
}