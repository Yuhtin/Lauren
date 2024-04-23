package com.yuhtin.lauren.commands.impl.utility;

import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import com.yuhtin.lauren.commands.CommandType;
import com.yuhtin.lauren.module.Module;
import com.yuhtin.lauren.module.impl.leaderboard.ExperiencieLeaderboardModule;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

import java.time.Instant;

@CommandInfo(
        name = "xp.top",
        type = CommandType.UTILITY,
        description = "Ver os jogadores mais viciados no meu servidor"
)
public class TopXpCommand implements Command {

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) throws Exception {
        val builder = new EmbedBuilder();

        ExperiencieLeaderboardModule module = Module.instance(ExperiencieLeaderboardModule.class);
        if (module == null) {
            hook.sendMessage("O módulo de leaderboard de experiência não está carregado!").queue();
            return;
        }

        builder.setAuthor("Jogadores mais viciados", null, event.getGuild().getIconUrl());
        builder.setFooter("Comando usado às", event.getUser().getAvatarUrl());
        builder.setDescription(module.getLeaderboard());
        builder.setTimestamp(Instant.now());

        hook.sendMessageEmbeds(builder.build()).queue();
    }
}
