package com.yuhtin.lauren.commands.impl.utility;

import com.google.inject.Inject;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import com.yuhtin.lauren.tasks.TopXpUpdater;
import net.dv8tion.jda.api.EmbedBuilder;

import java.time.Instant;

@CommandHandler(
        name = "topxp",
        type = CommandHandler.CommandType.UTILITY,
        description = "Ver os jogadores mais viciados no meu servidor",
        alias = {"xptop", "topplayers", "top"}
)
public class TopXpCommand extends Command {

    @Inject private TopXpUpdater topXpUpdater;

    @Override
    protected void execute(CommandEvent event) {

        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor("Jogadores mais viciados", null, event.getGuild().getIconUrl());
        builder.setFooter("Comando usado às", event.getAuthor().getAvatarUrl());
        builder.setDescription(this.topXpUpdater.getTopPlayers());
        builder.setTimestamp(Instant.now());

        event.getChannel().sendMessage(builder.build()).queue();

    }
}
