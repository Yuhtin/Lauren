package com.yuhtin.lauren.commands.utility;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.service.PlayerService;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import com.yuhtin.lauren.utils.helper.MathUtils;
import com.yuhtin.lauren.utils.helper.Utilities;

import java.util.concurrent.TimeUnit;

@CommandHandler(
        name = "daily",
        type = CommandHandler.CommandType.UTILITY,
        description = "Pegar uma pequena quantia de XP e dinheiro diariamente",
        alias = {"diario", "d", "dly", "diaria"})
public class DailyCommand extends Command {

    public DailyCommand() {
        this.name = "daily";
        this.aliases = new String[]{"diario", "d", "dly", "diaria"};
    }

    @Override
    protected void execute(CommandEvent event) {
        Player data = PlayerService.INSTANCE.get(event.getMember().getIdLong());

        if (data.dailyDelay > System.currentTimeMillis()) {
            event.getChannel().sendMessage("Poxa 😥 Você precisa aguardar mais `"
                    + MathUtils.format(data.dailyDelay - System.currentTimeMillis()) + "` para usar este comando novamente").queue();
            return;
        }

        double bonus = Utilities.isBooster(event.getMember()) ? 1.5 : 1;

        data.setDelay(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1)).addMoney(15 * bonus).gainXP(100 * bonus).save();
        event.getChannel().sendMessage("🌟 Aaaaa, eu to muito feliz por ter lembrado de mim e pego seu daily 💙 Veja suas informações atualizadas usando `$perfil` ").queue();
    }
}
