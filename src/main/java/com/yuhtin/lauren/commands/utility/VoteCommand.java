package com.yuhtin.lauren.commands.utility;

import com.google.inject.Inject;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import com.yuhtin.lauren.utils.helper.TimeUtils;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@CommandHandler(
        name = "vote",
        type = CommandHandler.CommandType.UTILITY,
        description = "Ver as informações de seus votos",
        alias = {"votar"}
)
public class VoteCommand extends Command {

    @Inject private PlayerController playerController;

    @Override
    protected void execute(CommandEvent event) {

        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setAuthor("Informações de votos", null, event.getGuild().getIconUrl());
        embedBuilder.setFooter("| Todos os direitos reservados", event.getGuild().getIconUrl());
        embedBuilder.setColor(Color.GRAY);

        Player player = this.playerController.get(event.getAuthor().getIdLong());
        String voteStatus = System.currentTimeMillis() > player.getVoteDelay()
                ? "<:online:703089222021808170> Você já pode votar novamente"
                : "<:nao_pertubar:703089222185386056> Você precisa aguardar mais "
                + TimeUtils.formatTime(player.getVoteDelay() - System.currentTimeMillis());

        embedBuilder.setDescription("✨ Bem vindo ao sistema de votação da Lauren ✨\n\n" +
                "Aqui você ganha algumas recompensas ajudando nosso servidor.\n" +
                "<:bronzekey:775100121322356766> Recompensas disponíveis:\n" +
                "  - <:xp:772285036174639124> 250-500 XP\n" +
                "  - <:boost_emoji:772285522852839445> 75-150 shards\n" +
                "  - <:sopa:756767328523517982> Tempo do $daily resetado\n\n" +
                voteStatus + "\n\n" +
                "<:version:756767328334512179> [Vote aqui](https://top.gg/servers/700673055982354472/vote)"
        );

        event.getChannel().sendMessage(embedBuilder.build()).queue();

    }
}
