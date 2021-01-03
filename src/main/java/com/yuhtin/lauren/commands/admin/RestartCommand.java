package com.yuhtin.lauren.commands.admin;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.LaurenStartup;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import com.yuhtin.lauren.utils.helper.Utilities;

@CommandHandler(
        name = "restart",
        type = CommandHandler.CommandType.CONFIG,
        description = "Reiniciar meus sistemas :d",
        alias = {"reiniciar"})
public class RestartCommand extends Command {

    @Override
    protected void execute(CommandEvent event) {
        if (!Utilities.INSTANCE.isOwner(event.getChannel(), event.getMember().getUser(), true)) return;

        event.getChannel().sendMessage("Reiniciando meus sistemas :satisfied:").queue();
        Logger.log("The player " + event.getMember().getUser().getName() + " restarting my systems");

        LaurenStartup.finish();
    }
}
