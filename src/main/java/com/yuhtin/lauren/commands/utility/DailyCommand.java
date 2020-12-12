package com.yuhtin.lauren.commands.utility;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.core.statistics.controller.StatsController;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import com.yuhtin.lauren.utils.helper.MathUtils;
import com.yuhtin.lauren.utils.helper.TimeUtils;

import java.util.concurrent.TimeUnit;

@CommandHandler(
        name = "daily",
        type = CommandHandler.CommandType.UTILITY,
        description = "Pegar uma pequena quantia de XP e dinheiro diariamente",
        alias = {"diario", "d", "dly", "diaria"})
public class DailyCommand extends Command {

    @Override
    protected void execute(CommandEvent event) {
        Player data = PlayerController.INSTANCE.get(event.getMember().getIdLong());

        if (data.getDailyDelay() > System.currentTimeMillis()) {
            event.getChannel().sendMessage("Poxa 😥 Você precisa aguardar mais `"
                    + TimeUtils.formatTime(data.getDailyDelay() - System.currentTimeMillis()) + "` para usar este comando novamente").queue();
            return;
        }

        data.setDelay(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1)).addMoney(45).gainXP(100);
        event.getChannel()
                .sendMessage("🌟 Aaaaa, eu to muito feliz por ter lembrado de mim e pego seu daily " +
                        "💙 Veja suas informações atualizadas usando `$perfil`")
                .queue();
        StatsController.get().getStats("Daily Command").suplyStats(1);
    }
}
