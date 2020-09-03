package com.yuhtin.lauren.commands.suport.draw;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.core.draw.controller.DrawController;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import com.yuhtin.lauren.utils.helper.Utilities;
import net.dv8tion.jda.api.Permission;

@CommandHandler(
        name = "reroll",
        type = CommandHandler.CommandType.SUPORT,
        description = "Sortear um ganhador novamente",
        alias = {"resortear", "redraw"})
public class RerollCommand extends Command {

    @Override
    protected void execute(CommandEvent event) {
        if (!Utilities.INSTANCE.isPermission(event.getMember(), event.getChannel(), Permission.ADMINISTRATOR, true)) return;

        if (DrawController.get() == null || !DrawController.get().finished) {
            event.getMessage().delete().queue();
            return;
        }

        event.getChannel().sendMessage("♻️ Eu realmente não sei o que aconteceu, mas, como mandaram, sorteando um novo vencedor").queue();
        DrawController.get().finish();
    }
}
