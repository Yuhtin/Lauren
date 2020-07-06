package com.yuhtin.lauren.events;

import com.yuhtin.lauren.core.player.controller.PlayerDataController;
import com.yuhtin.lauren.service.CommandCache;
import com.yuhtin.lauren.utils.helper.LevenshteinCalculator;
import com.yuhtin.lauren.utils.helper.Utilities;
import net.dv8tion.jda.api.Permission;
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

        if (event.getMessage().getContentRaw().contains("https://") && Utilities.isPermission(event.getMember(), event.getChannel(), Permission.MESSAGE_MANAGE)) {
            event.getChannel().sendMessage("<:chorano:726207542413230142> Poxa, não divulga aqui amigo, temos nosso sistema de parceria, fale com o <@272879983326658570> no privado.").queue();
            event.getMessage().delete().queue();
            return;
        }

        PlayerDataController.get(event.getMember().getIdLong()).gainXP(3).updateLevel().save();
    }
}