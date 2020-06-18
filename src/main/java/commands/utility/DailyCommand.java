package commands.utility;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import models.annotations.CommandHandler;
import models.cache.PlayerDataCache;
import models.data.PlayerData;
import utils.helper.MathUtils;

import java.util.concurrent.TimeUnit;

@CommandHandler(name = "daily", type = CommandHandler.CommandType.UTILITY, description = "Pegar uma pequena quantia de XP e dinheiro diariamente")
public class DailyCommand extends Command {

    public DailyCommand() {
        this.name = "daily";
        this.aliases = new String[]{"diario", "d", "dly", "diaria"};
    }

    @Override
    protected void execute(CommandEvent event) {
        PlayerData data = PlayerDataCache.get(event.getMember());
        if (data.dailyDelay > System.currentTimeMillis()) {
            event.getChannel().sendMessage("Poxa 😥 Você precisa aguardar mais `"
                    + MathUtils.format(data.dailyDelay - System.currentTimeMillis()) + "` para usar este comando novamente").queue();
            return;
        }

        double bonus = event.getMember().getRoles().contains(event.getJDA().getRoleById(722116789055782912L)) ? 1.5 : 1;

        data.setDelay(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1)).addMoney(15 * bonus).gainXP(100 * bonus).save();
        event.getChannel().sendMessage("🌟 Aaaaa, eu to muito feliz por ter lembrado de mim e pego seu daily 💙 Veja suas informações atualizadas usando `$perfil`").queue();
    }
}
