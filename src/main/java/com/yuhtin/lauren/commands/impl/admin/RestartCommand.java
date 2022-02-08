package com.yuhtin.lauren.commands.impl.admin;

import com.google.inject.Inject;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.commands.CommandHandler;
import com.yuhtin.lauren.startup.Startup;
import com.yuhtin.lauren.utils.helper.UserUtil;

@CommandHandler(
        name = "restart",
        type = CommandHandler.CommandType.CONFIG,
        description = "Reiniciar meus sistemas :d",
        alias = {"reiniciar"})
public class RestartCommand implements CommandExecutor {

    @Inject private Logger logger;

    @Override
    public void execute(CommandEvent event) {
        if (!UserUtil.isOwner(event.getChannel(), event.getMember().getUser(), true)) return;

        event.getChannel().sendMessage("Reiniciando meus sistemas :satisfied:").queue();
        this.logger.info("The player " + event.getMember().getUser().getName() + " restarting my systems");

        Startup.getLauren().shutdown();
    }
}
