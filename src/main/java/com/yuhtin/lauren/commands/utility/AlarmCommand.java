package com.yuhtin.lauren.commands.utility;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.core.alarm.Alarm;
import com.yuhtin.lauren.core.alarm.controller.AlarmController;
import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import com.yuhtin.lauren.utils.helper.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;

import java.time.Instant;

@CommandHandler(
        name = "alarme",
        type = CommandHandler.CommandType.UTILITY,
        description = "Veja os alarmes do servidor",
        alias = {"alarm", "alarmes"}
)
public class AlarmCommand extends Command {

    @Override
    protected void execute(CommandEvent event) {
        String[] arguments = event.getArgs().split(" ");
        if (arguments.length == 0) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setAuthor("| Alarmes Registrados", null, event.getGuild().getIconUrl());
            embed.setTimestamp(Instant.now());
            embed.setFooter("Comando usado por " + Utilities.INSTANCE.getFullName(event.getMember().getUser()), event.getMember().getUser().getAvatarUrl());
            embed.setDescription("Todos os Alarmes do servidor: (" + AlarmController.get().getAlarms().size() + ")\n\n"
                    + AlarmController.get().getAlarms().keySet());

            event.getChannel().sendMessage(embed.build()).queue();
            return;
        }

        if (!AlarmController.get().getAlarms().containsKey(arguments[0])) {
            event.getChannel().sendMessage("<a:tchau:751941650728747140> Este alarme não existe, use `$alarme` para ver a lista completa").queue();
            return;
        }

        Alarm alarm = AlarmController.get().getAlarms().get(arguments[0]);
        Player player = PlayerController.INSTANCE.get(event.getAuthor().getIdLong());

        if (player.alarms.contains(alarm)) {
            player.alarmsName.remove(alarm.getName());
            player.alarms.remove(alarm);
            event.getChannel().sendMessage("<:poggers:751941649747411014> Você desativou o alarme `" + alarm.getName() + "` com sucesso").queue();
        }

        player.alarmsName.add(alarm.getName());
        player.alarms.add(alarm);
        event.getChannel().sendMessage("<:poggers:751941649747411014> Você ativou o alarme `" + alarm.getName() + "` com sucesso").queue();
    }
}
