package com.yuhtin.lauren.commands.scrim;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.core.match.controller.MatchController;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import com.yuhtin.lauren.utils.helper.Utilities;

import java.util.concurrent.TimeUnit;

@CommandHandler(
        name = "sairfila",
        type = CommandHandler.CommandType.SCRIM,
        description = "Serve para sair da fila de partida atual",
        alias = {"filasair", "leave"})
public class LeaveRowCommand extends Command {

    public LeaveRowCommand() {
        this.name = "sair";
        this.aliases = new String[]{"filasair", "leave"};
    }

    @Override
    protected void execute(CommandEvent event) {
        event.getMessage().delete().queue();

        if (!MatchController.playersInQueue.containsKey(event.getAuthor().getIdLong())) {
            event.getChannel().sendMessage("<a:palmas:726205273974374440> " + Utilities.INSTANCE.getFullName(event.getAuthor()) + ", você não está numa fila de partida")
                    .queue(message -> message.delete().queueAfter(3, TimeUnit.SECONDS));
            return;
        }

        MatchController.removePlayerFromRow(event.getMember().getIdLong());
        event.getChannel().sendMessage("<a:nao:704295026036834375> " + Utilities.INSTANCE.getFullName(event.getAuthor()) + ", você saiu da fila de partida")
                .queue(message -> message.delete().queueAfter(3, TimeUnit.SECONDS));
    }
}
