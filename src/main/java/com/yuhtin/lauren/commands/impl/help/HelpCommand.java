package com.yuhtin.lauren.commands.impl.help;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.commands.CommandHandler;
import com.yuhtin.lauren.service.CommandCache;
import net.dv8tion.jda.api.EmbedBuilder;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@CommandHandler(
        name = "ajuda",
        type = CommandHandler.CommandType.HELP,
        description = "Informações de comandos do bot",
        alias = {"help"})
public class HelpCommand extends Command {

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getMessage().getContentRaw().split(" ");

        if (args.length > 1) {

            if (!CommandCache.getCommands().containsKey(args[1].toLowerCase())) {

                event.getMessage().delete().queue();
                event.getChannel().sendMessage("Hmm, não encontrei o comando `" + args[1] + "` tente usar `$ajuda` para ver meus comandos.")
                        .queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
                return;

            }

            EmbedBuilder embed = CommandCache.getCommands().get(args[1].toLowerCase())
                    .setFooter("Comando usado por " + event.getMember().getEffectiveName(), event.getMember().getUser().getAvatarUrl())
                    .setColor(event.getMember().getColor())
                    .setTimestamp(Instant.now());

            event.getChannel().sendMessage(embed.build()).queue();
            return;

        }

        EmbedBuilder helpEmbed = CommandCache.getHelpEmbed();

        helpEmbed.setFooter("Comando usado por " + event.getMember().getEffectiveName(), event.getMember().getUser().getAvatarUrl())
                .setColor(event.getMember().getColor())
                .setTimestamp(Instant.now());

        event.getChannel().sendMessage(helpEmbed.build()).queue();

    }


}
