package com.yuhtin.lauren.commands.admin;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import com.yuhtin.lauren.startup.Startup;
import com.yuhtin.lauren.utils.helper.Utilities;

import java.util.logging.Logger;

@CommandHandler(
        name = "restart",
        type = CommandHandler.CommandType.CONFIG,
        description = "Reiniciar meus sistemas :d",
        alias = {"reiniciar"})
public class RestartCommand extends Command {

    @Inject @Named("main") private Logger logger;

    @Override
    protected void execute(CommandEvent event) {
        if (!Utilities.INSTANCE.isOwner(event.getChannel(), event.getMember().getUser(), true)) return;

        event.getChannel().sendMessage("Reiniciando meus sistemas :satisfied:").queue();
        this.logger.info("The player " + event.getMember().getUser().getName() + " restarting my systems");

        Startup.getLauren().shutdown();
    }
}
