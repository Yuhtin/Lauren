package com.yuhtin.lauren.commands;

import lombok.Getter;
import lombok.val;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.inject.Singleton;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
@Singleton
public final class CommandCatcher extends ListenerAdapter {

    @Getter private final CommandMap commandMap = new CommandMap();

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        val hook = event.deferReply().complete();
        val commands = commandMap.getCommands();

        val command = commands.get(event.getName());
        try {
            command.execute(event, hook);
        } catch (Exception exception) {
            exception.printStackTrace();
            hook.sendMessage("ERRO!").queue();
        }
    }
}
