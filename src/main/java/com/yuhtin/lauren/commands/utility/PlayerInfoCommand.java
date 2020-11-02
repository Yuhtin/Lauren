package com.yuhtin.lauren.commands.utility;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.core.statistics.controller.StatsController;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import com.yuhtin.lauren.models.enums.LogType;
import com.yuhtin.lauren.utils.helper.MathUtils;
import com.yuhtin.lauren.utils.helper.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

@CommandHandler(
        name = "perfil",
        type = CommandHandler.CommandType.SCRIM,
        description = "Visualizar o perfil de outro usuário",
        alias = {"pinfo", "jogador", "playerinfo", "player", "profile", "conta"})
public class PlayerInfoCommand extends Command {

    @Override
    protected void execute(CommandEvent event) {
        Member target = event.getMessage().getMentionedMembers().isEmpty() ? event.getMember() : event.getMessage().getMentionedMembers().get(0);
        Player player = PlayerController.INSTANCE.get(target.getIdLong());
        if (player == null) {

            Logger.log("Occured an error on try load player data of " + target.getIdLong(), LogType.ERROR).save();

            event.getChannel().sendMessage("Ocorreu um erro em meus dados, defusa aqui <@272879983326658570>").queue();
            event.getChannel().sendMessage("Player ID: " + target.getIdLong()).queue();

            return;

        }

        String roles = Utilities.INSTANCE.rolesToString(target.getRoles());
        String name = target.getNickname() == null ? target.getUser().getName() : target.getNickname();
        String userDate = event.getMessage().getMember() == null ? "Erro" : subtractTime(target.getTimeJoined());

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(target.getColor());
        embed.setAuthor("Informações do jogador " + name, null, target.getUser().getAvatarUrl());
        embed.setThumbnail(player.getRank().getUrl());

        embed.addField("⚗️ Experiência", "`Nível " + player.getLevel() + " (" + Utilities.INSTANCE.format(player.getExperience()) + " XP)`", false);
        embed.addField("🧶 Cargos", "`" + (roles.equalsIgnoreCase("") ? "Nenhum" : roles) + "`", false);
        embed.addField("✨ Entrou em", userDate, false);
        embed.addField("\uD83D\uDC7E Eventos", "`" + player.getTotalEvents() + "`", false);
        embed.addField("<:boost_emoji:772285522852839445> Shards", "`$" + (Utilities.INSTANCE.format(player.getMoney())) + " shards`", true);
        embed.addField("<:beacon:771543538252120094> Patente", "`" + player.getRank().getName() + "`", true);
        embed.addField("<:lootbox:771545027829563402> LootBoxes", "`" + player.getLootBoxes() + " caixas`", true);
        embed.addField("\uD83D\uDD11 Chaves", "`" + player.getKeys() + " keys`", true);

        embed.setFooter("Comando usado por " + event.getMember().getNickname(), event.getMember().getUser().getAvatarUrl());
        embed.setTimestamp(Instant.now());

        event.getChannel().sendMessage(embed.build()).queue();

        StatsController.get().getStats("Player Command").suplyStats(1);
    }

    private String subtractTime(OffsetDateTime before) {
        return before.getDayOfMonth() + " de " + before.getMonth().getDisplayName(TextStyle.SHORT, Locale.US) + ", "
                + before.getYear() + " às " + before.getHour() + ":" + before.getMinute() +
                " (" + MathUtils.format(System.currentTimeMillis() - before.toInstant().toEpochMilli()) + ")";
    }
}
