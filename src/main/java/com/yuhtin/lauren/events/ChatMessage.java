package com.yuhtin.lauren.events;

import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.service.CommandCache;
import com.yuhtin.lauren.utils.helper.LevenshteinCalculator;
import com.yuhtin.lauren.utils.helper.Utilities;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.TimeUnit;

public class ChatMessage extends ListenerAdapter {

    /* Earn 3 XP for every message sent */
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getChannelType().isGuild()
                || event.getMember() == null
                || event.getAuthor().isBot())
            return;

        if (event.getMessage().getContentRaw().startsWith("$")) {
            String command = event.getMessage().getContentRaw().split(" ")[0].replace("$", "").toLowerCase();
            if (!CommandCache.aliases.contains(command)) {
                for (String alias : CommandCache.aliases) {
                    if (LevenshteinCalculator.eval(command, alias) < 4) {
                        event.getChannel().sendMessage("<:chorano:726207542413230142> Esse comandinho não existe porém encontrei um parecido: `$" + alias + "`").queue();
                        break;
                    }
                }
            }
        }

        if (!isMusicCommand(event.getMessage().getContentRaw())
                && event.getMessage().getContentRaw().contains("https://")
                && !Utilities.INSTANCE.isPermission(event.getMember(), event.getChannel(), Permission.MESSAGE_MANAGE, false)) {

            event.getChannel()
                    .sendMessage("<:chorano:726207542413230142> Poxa, não divulga aqui amigo, temos nosso sistema de parceria, fale com o <@272879983326658570> no privado.")
                    .queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
            event.getMessage().delete().queue();
            return;
        }

        PlayerController.INSTANCE.get(event.getMember().getIdLong()).gainXP(3).updateLevel();
    }

    private boolean isMusicCommand(String contentRaw) {
        return contentRaw.startsWith("$m ") || contentRaw.startsWith("$music") || contentRaw.startsWith("$play ") || contentRaw.startsWith("$tocar ");
    }


}