package com.yuhtin.lauren.commands.impl.utility;

import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import com.yuhtin.lauren.commands.CommandType;
import com.yuhtin.lauren.module.Module;
import com.yuhtin.lauren.module.impl.player.module.PlayerModule;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

@CommandInfo(
        name = "daily",
        type = CommandType.UTILITY,
        description = "Pegar uma pequena quantia de XP e dinheiro diariamente"
)
public class DailyCommand implements Command {

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) throws Exception {
        PlayerModule playerModule = Module.instance(PlayerModule.class);
        if (playerModule == null) {
            hook.sendMessage("Ops, ocorreu um erro ao tentar realizar esta ação").queue();
            return;
        }

        playerModule.retrieve(event.getMember().getIdLong()).thenAccept(player -> {
            if (!player.isAbbleToDaily()) {
                hook.sendMessage("Poxa 😥 Você precisa aguardar até 12:00 para usar este comando novamente").queue();
                return;
            }

            player.setAbbleToDaily(false);
            player.addMoney(75);
            player.gainXP(300);

            hook.sendMessage(
                    "🌟 Aaaaa, eu to muito feliz por ter lembrado de mim e pego seu daily " +
                            "💙 Veja suas informações atualizadas usando `/perfil`"
            ).queue();

            // TODO: statsController.getStats("Daily Command").suplyStats(1);
        });
    }
}
