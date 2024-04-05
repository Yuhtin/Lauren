package com.yuhtin.lauren.commands.impl.admin;

import com.google.inject.Inject;
import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.startup.Startup;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

@CommandInfo(
        name = "restart",
        type = CommandInfo.CommandType.CONFIG,
        description = "Reiniciar meus sistemas :d"
)
public class RestartCommand implements Command {

    @Inject private Logger logger;

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) throws Exception {
        if (!UserUtil.isOwner(event.getUser(), hook)) return;

        hook.sendMessage("Reiniciando meus sistemas :satisfied:").queue();
        logger.info("The player " + event.getUser().getName() + " restarting my systems");

        Startup.getLauren().shutdown();
    }

}
