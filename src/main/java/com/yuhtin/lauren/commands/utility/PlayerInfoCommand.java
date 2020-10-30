package com.yuhtin.lauren.commands.utility;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.core.statistics.controller.StatsController;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import com.yuhtin.lauren.core.player.controller.PlayerController;
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
        alias = {"pinfo", "jogador", "playerinfo", "player"})
public class PlayerInfoCommand extends Command {

    @Override
    protected void execute(CommandEvent event) {
        Member target = event.getMessage().getMentionedMembers().isEmpty() ? event.getMember() : event.getMessage().getMentionedMembers().get(0);
        Player controller = PlayerController.INSTANCE.get(target.getIdLong());
        if (controller == null) {
            Logger.log("Occured an error on try load player data of " + target.getIdLong(), LogType.ERROR).save();
            event.getChannel().sendMessage("Ocorreu um erro em meus dados, defusa aqui <@272879983326658570>").queue();

            event.getChannel().sendMessage("Player ID: " + target.getIdLong()).queue();

            return;
        }

        String roles = Utilities.INSTANCE.rolesToString(target.getRoles());
        String name = target.getNickname() == null ? target.getUser().getName() : target.getNickname();
        String userDate = event.getMessage().getMember() == null ? "Erro" : subtractTime(target.getTimeJoined());

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(target.getColor())
                .setAuthor("Informações do jogador " + name, null, target.getUser().getAvatarUrl())
                .setThumbnail(controller.getRank().getUrl())

                .addField("⚗️ Experiência", "`Nível " + controller.level + " (" + Utilities.INSTANCE.format(controller.experience) + " XP)`", false)
                .addField("🧶 Cargos", "`" + (roles.equalsIgnoreCase("") ? "Nenhum" : roles) + "`", false)
                .addField("✨ Entrou em", userDate, false)
                .addField("\uD83D\uDCB0 Dinheiro", "`$" + (Utilities.INSTANCE.format(controller.money)) + "`", false)
                .addField("\uD83D\uDC7E Eventos", "`" + controller.totalEvents + "`", false)
                .addField("<:beacon:771543538252120094> Patente", "`" + controller.getRank().getName() + "`", false)
                .addField("<:lootbox:771545027829563402> LootBoxes", "`" + controller.lootBoxes + " caixas`", false)

                .setFooter("Comando usado por " + event.getMember().getNickname(), event.getMember().getUser().getAvatarUrl())
                .setTimestamp(Instant.now());

        event.getChannel().sendMessage(embedBuilder.build()).queue();

        StatsController.get().getStats("Player Command").suplyStats(1);
    }

    private String subtractTime(OffsetDateTime before) {
        return before.getDayOfMonth() + " de " + before.getMonth().getDisplayName(TextStyle.SHORT, Locale.US) + ", "
                + before.getYear() + " às " + before.getHour() + ":" + before.getMinute() +
                " (" + MathUtils.format(System.currentTimeMillis() - before.toInstant().toEpochMilli()) + ")";
    }
}
