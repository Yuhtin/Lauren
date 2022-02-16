package com.yuhtin.lauren.commands.impl.utility;

import com.google.inject.Inject;
import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.core.statistics.StatsController;
import com.yuhtin.lauren.utils.DateUtil;
import com.yuhtin.lauren.utils.NumberUtils;
import com.yuhtin.lauren.utils.UserUtil;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

import java.time.Instant;

@CommandInfo(
        name = "perfil",
        type = CommandInfo.CommandType.SCRIM,
        description = "Visualizar o seu perfil ou de outro usuário",
        args = {
                "[@user]-Usuário que você deseja ver o perfil"
        }
)
public class ProfileCommand implements Command {

    @Inject private Logger logger;
    @Inject private PlayerController playerController;
    @Inject private StatsController statsController;

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) throws Exception {
        val userOption = event.getOption("user");
        val target = userOption == null ? event.getMember() : userOption.getAsMember();
        val player = playerController.get(target.getIdLong());
        if (player == null) {
            logger.severe("Occured an error on try load player data of " + target.getIdLong());

            hook.sendMessage("Ocorreu um erro em meus dados, defusa aqui <@272879983326658570>\nPlayer ID: " + target.getIdLong()).queue();
            return;
        }

        val roles = UserUtil.rolesToString(target.getRoles());
        val name = target.getNickname() == null ? target.getUser().getName() : target.getNickname();

        val embed = new EmbedBuilder();
        embed.setColor(target.getColor());
        embed.setAuthor("Informações do jogador " + name, null, target.getUser().getAvatarUrl());
        embed.setThumbnail(player.getRank().getUrl());

        embed.addField("⚗️ Experiência", "`Nível " + player.getLevel() + " (" + NumberUtils.format(player.getExperience()) + " XP)`", false);
        embed.addField("🧶 Cargos", "`" + (roles.equalsIgnoreCase("") ? "Nenhum" : roles) + "`", false);
        embed.addField("✨ Entrou em", DateUtil.format(target.getTimeJoined().toEpochSecond()), false);
        embed.addField("\uD83D\uDC7E Eventos", "`" + player.getTotalEvents() + "`", false);
        embed.addField("<:boost_emoji:772285522852839445> Shards", "`$" + NumberUtils.format(player.getMoney()) + " shards`", true);
        embed.addField("<:beacon:771543538252120094> Patente", "`" + player.getRank().getName() + "`", true);
        embed.addField("<:lootbox:771545027829563402> LootBoxes", "`" + player.getLootBoxes() + " caixas`", true);
        embed.addField("\uD83D\uDD11 Chaves", "`" + player.getKeys() + " keys`", true);
        embed.addField("<:rename_command:775348818555699210> Votos no servidor", "`" + player.getVotes() + " votos`", true);

        embed.setFooter("Comando usado por " + name, event.getMember().getUser().getAvatarUrl());
        embed.setTimestamp(Instant.now());

        hook.sendMessageEmbeds(embed.build()).queue();
        statsController.getStats("Player Command").suplyStats(1);
    }
}
