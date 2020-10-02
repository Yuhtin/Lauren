package com.yuhtin.lauren.commands.utility;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.statistics.StatsInfo;
import com.yuhtin.lauren.core.statistics.controller.StatsController;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import net.dv8tion.jda.api.EmbedBuilder;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

@CommandHandler(
        name = "stats",
        type = CommandHandler.CommandType.UTILITY,
        description = "Estatísticas de alguns sistemas meus",
        alias = {"estatisticas"}
)
public class StatsCommand extends Command {

    @Override
    protected void execute(CommandEvent event) {
        String args = event.getArgs();
        if (args.equalsIgnoreCase("")) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setAuthor("| Todas as estatísticas da Lauren", null, event.getGuild().getIconUrl());
            builder.setDescription(
                    "Para ver uma estatística completa, use `$stats <nome>`\n\n" +
                            "Todas as estatísticas: " + StatsController.get().getStats().keySet());

            builder.setFooter("Comando usado as", event.getAuthor().getAvatarUrl());
            builder.setTimestamp(Instant.now());

            event.getChannel().sendMessage(builder.build()).queue();
            return;
        }

        StatsInfo info = StatsController.get().getStats().getOrDefault(args, null);
        if (info == null) {
            event.getChannel().sendMessage("⚡ Não encontrei nenhuma estatística relacionada").queue();
            return;
        }


        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int month = calendar.get(Calendar.MONTH),
                year = calendar.get(Calendar.YEAR);

        int lastMonth = month - 1,
                lastYear = year;

        if (lastMonth < 0) {
            lastMonth = 12;
            --lastYear;
        }

        EmbedBuilder builder = new EmbedBuilder();
        builder.addField("🚓 Estatística", "`" + info.getName() + "`", false);
        builder.addField("🚀 Total", "`" + info.getTotalStats() + " usos`", false);
        builder.addField("🍕 Este mês", "`" + info.getStats(month + "/" + year) + " usos`", false);
        builder.addField("✈ Último mês", "`" + info.getStats(lastMonth + "/" + lastYear) + " usos`", false);

        builder.setAuthor("| Todas as estatísticas da Lauren", null, event.getGuild().getIconUrl());
        builder.setFooter("Comando usado as", event.getAuthor().getAvatarUrl());
        builder.setTimestamp(Instant.now());

        event.getChannel().sendMessage(builder.build()).queue();
        StatsController.get().getStats("Análise de Estatísticas").suplyStats(1);
    }
}
