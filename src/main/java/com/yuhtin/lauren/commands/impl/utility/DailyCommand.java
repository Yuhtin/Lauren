package com.yuhtin.lauren.commands.impl.utility;

import com.google.inject.Inject;
import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.core.statistics.StatsController;
import lombok.val;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

@CommandInfo(
        name = "daily",
        type = CommandType.UTILITY,
        description = "Pegar uma pequena quantia de XP e dinheiro diariamente"
)
public class DailyCommand implements Command {

    @Inject private PlayerController playerController;
    @Inject private StatsController statsController;

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) throws Exception {
        val data = playerController.get(event.getMember().getIdLong());
        if (!data.isAbbleToDaily()) {
            hook.sendMessage("Poxa 😥 Você precisa aguardar até 12:00 para usar este comando novamente").queue();
            return;
        }

        data.setAbbleToDaily(false).addMoney(75).gainXP(300);
        hook.sendMessage(
                "🌟 Aaaaa, eu to muito feliz por ter lembrado de mim e pego seu daily " +
                "💙 Veja suas informações atualizadas usando `/perfil`"
        ).queue();

        statsController.getStats("Daily Command").suplyStats(1);
    }
}
