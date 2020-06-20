package com.yuhtin.lauren.commands.admin;

import com.yuhtin.lauren.application.Lauren;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import net.dv8tion.jda.api.Permission;
import com.yuhtin.lauren.utils.helper.Utilities;

@CommandHandler(name = "restart", type = CommandHandler.CommandType.CONFIG, description = "Reiniciar meus sistemas :d")
public class RestartCommand extends Command {

    public RestartCommand() {
        this.name = "restart";
        this.aliases = new String[]{"reiniciar"};
    }

    @Override
    protected void execute(CommandEvent event) {
        if (!Utilities.isOwner(event.getChannel(), event.getMember().getUser())) return;

        Logger.log("The player " + event.getMember().getUser().getName() + " restarting my systems").save();
        event.getChannel().sendMessage("Reiniciando meus sistemas :satisfied:").queue();
        Lauren.finish();
    }
}
