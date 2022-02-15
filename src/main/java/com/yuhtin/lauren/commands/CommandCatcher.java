package com.yuhtin.lauren.commands;

import com.yuhtin.lauren.startup.Startup;
import com.yuhtin.lauren.utils.SimpleEmbed;
import lombok.Getter;
import lombok.val;
import lombok.var;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.inject.Singleton;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
@Singleton
public final class CommandCatcher extends ListenerAdapter {

    @Getter
    private final CommandMap commandMap = new CommandMap();

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getChannel().getType() != ChannelType.TEXT) {
            event.replyEmbeds(SimpleEmbed.of("Você só pode usar meus comandos em servidores.")).queue();
            return;
        }

        event.deferReply().queue(hook -> {
            val commands = commandMap.getCommands();

            var name = event.getName();
            val subcommandName = event.getSubcommandName();
            if (subcommandName != null) {
                name += "." + subcommandName;
            }

            val logger = Startup.getLauren().getLogger();
            val command = commands.getOrDefault(name, null);
            if (command == null) {
                logger.info("Não encontrei o comando " + name);
                return;
            }

            try {
                command.execute(event, hook);
            } catch (Exception exception) {
                exception.printStackTrace();
                hook.sendMessage("ERRO!").queue();
            }
        });

    }
}
