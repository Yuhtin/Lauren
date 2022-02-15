package com.yuhtin.lauren.commands.impl.utility;

import com.google.inject.Inject;
import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.statistics.StatsController;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

@CommandInfo(
        name = "stats",
        type = CommandInfo.CommandType.UTILITY,
        description = "Estatísticas de alguns sistemas meus",
        args = {
                "[stat]-Estatística que deseja ver"
        }
)
public class StatsCommand implements Command {

    @Inject
    private Logger logger;
    @Inject
    private StatsController statsController;

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) throws Exception {
        val statOption = event.getOption("stat");
        if (statOption == null) {
            val builder = new EmbedBuilder();
            builder.setAuthor("| Todas as estatísticas da Lauren", null, event.getGuild().getIconUrl());
            builder.setDescription(
                    "Para ver uma estatística completa, use `/stats`\n\n" +
                            "Todas as estatísticas: " + this.statsController.getStats().keySet());

            builder.setFooter("Comando usado as", event.getUser().getAvatarUrl());
            builder.setTimestamp(Instant.now());

            hook.sendMessageEmbeds(builder.build()).queue();
            return;
        }

        val info = this.statsController.getStats().getOrDefault(statOption.getAsString(), null);
        if (info == null) {
            hook.sendMessage("⚡ Não encontrei nenhuma estatística relacionada").queue();
            return;
        }

        val date = new Date();
        val calendar = Calendar.getInstance();
        calendar.setTime(date);

        int month = calendar.get(Calendar.MONTH),
                year = calendar.get(Calendar.YEAR);

        int lastMonth = month - 1,
                lastYear = year;

        if (lastMonth < 0) {
            lastMonth = 12;
            --lastYear;
        }

        val builder = new EmbedBuilder();
        builder.addField("🚓 Estatística", "`" + info.getName() + "`", false);
        builder.addField("🚀 Total", "`" + info.getTotalStats() + " usos`", false);
        builder.addField("🍕 Este mês", "`" + info.getStats(month + "/" + year) + " usos`", false);
        builder.addField("✈ Último mês", "`" + info.getStats(lastMonth + "/" + lastYear) + " usos`", false);

        builder.setAuthor("| Todas as estatísticas da Lauren", null, event.getGuild().getIconUrl());
        builder.setFooter("Comando usado as", event.getUser().getAvatarUrl());
        builder.setTimestamp(Instant.now());

        hook.sendMessageEmbeds(builder.build()).queue();
        statsController.getStats("Análise de Estatísticas").suplyStats(1);
    }

}
