package com.yuhtin.lauren.commands.utility;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import com.yuhtin.lauren.models.objects.CommonCommand;
import com.yuhtin.lauren.tasks.TopXpUpdater;
import net.dv8tion.jda.api.EmbedBuilder;

import java.time.Instant;

@CommandHandler(
        name = "topxp",
        type = CommandHandler.CommandType.UTILITY,
        description = "Ver os jogadores mais viciados no meu servidor",
        alias = {"xptop", "topplayers", "top"}
)
public class TopXpCommand extends CommonCommand {

    @Override
    protected void executeCommand(CommandEvent event) {
        event.getChannel().sendMessage("<a:carregando:766649080003821609> Procurando uma resposta boa pra te dar").queue();
        event.getChannel().sendTyping().queue();

        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor("Jogadores mais viciados", null, event.getGuild().getIconUrl());
        builder.setFooter("Comando usado às", event.getAuthor().getAvatarUrl());
        builder.setDescription(TopXpUpdater.getInstance().getTopPlayers());
        builder.setTimestamp(Instant.now());

        event.getChannel().sendMessage(builder.build()).queue();
    }
}
