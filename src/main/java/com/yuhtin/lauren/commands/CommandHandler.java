package com.yuhtin.lauren.commands;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.val;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
@EqualsAndHashCode(callSuper = true)
@Data
public final class CommandHandler extends ListenerAdapter {

    private final com.yuhtin.supremo.ticketbot.command.CommandMap commandMap;

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        val contentDisplay = event.getMessage().getContentDisplay();

        val commands = commandMap.getCommands();
        val args = contentDisplay.split(" ");

        val command = commands.getOrDefault(args[0], null);
        if (command == null) return;

        val stringBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            stringBuilder.append(args[i]);
            if (i + 1 < args.length) stringBuilder.append(" ");
        }

        command.execute(event.getMessage(), stringBuilder.toString());
    }

}
