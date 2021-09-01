package com.yuhtin.lauren.commands.impl.admin;

import com.google.inject.Inject;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.commands.CommandHandler;
import com.yuhtin.lauren.startup.Startup;
import com.yuhtin.lauren.utils.helper.UserUtil;

@CommandHandler(
        name = "restart",
        type = CommandHandler.CommandType.CONFIG,
        description = "Reiniciar meus sistemas :d",
        alias = {"reiniciar"})
public class RestartCommand extends Command {

    @Inject private Logger logger;

    @Override
    protected void execute(CommandEvent event) {
        if (!UserUtil.INSTANCE.isOwner(event.getChannel(), event.getMember().getUser(), true)) return;

        event.getChannel().sendMessage("Reiniciando meus sistemas :satisfied:").queue();
        this.logger.info("The player " + event.getMember().getUser().getName() + " restarting my systems");

        Startup.getLauren().shutdown();
    }
}
