package com.yuhtin.lauren.commands.impl.utility;

import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import com.yuhtin.lauren.commands.CommandType;
import com.yuhtin.lauren.module.Module;
import com.yuhtin.lauren.module.impl.player.module.PlayerModule;
import com.yuhtin.lauren.util.TimeUtils;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

import java.awt.*;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@CommandInfo(
        name = "votar",
        type = CommandType.UTILITY,
        description = "Ver as informações de seus votos"
)
public class VoteCommand implements Command {

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) throws Exception {
        val embedBuilder = new EmbedBuilder();

        embedBuilder.setAuthor("Informações de votos", null, event.getGuild().getIconUrl());
        embedBuilder.setFooter("| Todos os direitos reservados", event.getGuild().getIconUrl());
        embedBuilder.setColor(Color.GRAY);

        PlayerModule playerModule = Module.instance(PlayerModule.class);
        if (playerModule == null) {
            hook.sendMessage("⚡ Ocorreu um erro ao tentar buscar suas informações").queue();
            return;
        }

        playerModule.retrieve(event.getUser().getIdLong()).thenAccept(player -> {
            val voteStatus = System.currentTimeMillis() > player.getVoteDelay()
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

            hook.sendMessageEmbeds(embedBuilder.build()).queue();
        });
    }

}
