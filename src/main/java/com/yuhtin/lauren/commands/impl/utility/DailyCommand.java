package com.yuhtin.lauren.commands.impl.utility;

import com.google.inject.Inject;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.core.statistics.StatsController;
import com.yuhtin.lauren.models.annotations.CommandHandler;

@CommandHandler(
        name = "daily",
        type = CommandHandler.CommandType.UTILITY,
        description = "Pegar uma pequena quantia de XP e dinheiro diariamente",
        alias = {"diario", "d", "dly", "diaria"}
)
public class DailyCommand extends Command {

    @Inject private PlayerController playerController;
    @Inject private StatsController statsController;

    @Override
    protected void execute(CommandEvent event) {

        Player data = this.playerController.get(event.getMember().getIdLong());
        if (!data.isAbbleToDaily()) {

            event.getChannel().sendMessage("Poxa 😥 Você precisa aguardar até 12:00 para usar este comando novamente").queue();
            return;

        }

        data.setAbbleToDaily(false).addMoney(75).gainXP(300);
        event.getChannel()
                .sendMessage("🌟 Aaaaa, eu to muito feliz por ter lembrado de mim e pego seu daily " +
                        "💙 Veja suas informações atualizadas usando `$perfil`")
                .queue();

        this.statsController.getStats("Daily Command").suplyStats(1);

    }
}
