package com.yuhtin.lauren.commands.impl.utility;

import com.google.inject.Inject;
import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import com.yuhtin.lauren.tasks.TopXpUpdater;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

import java.time.Instant;

@CommandInfo(
        name = "xp.top",
        type = CommandInfo.CommandType.UTILITY,
        description = "Ver os jogadores mais viciados no meu servidor"
)
public class TopXpCommand implements Command {

    @Inject private TopXpUpdater topXpUpdater;

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) throws Exception {
        val builder = new EmbedBuilder();

        builder.setAuthor("Jogadores mais viciados", null, event.getGuild().getIconUrl());
        builder.setFooter("Comando usado às", event.getUser().getAvatarUrl());
        builder.setDescription(topXpUpdater.getTopPlayers());
        builder.setTimestamp(Instant.now());

        hook.sendMessageEmbeds(builder.build()).queue();
    }
}
