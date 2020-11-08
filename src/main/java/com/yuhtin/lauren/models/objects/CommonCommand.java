package com.yuhtin.lauren.models.objects;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.utils.helper.Utilities;

public abstract class CommonCommand extends Command {

    @Override
    protected void execute(CommandEvent event) {
        Logger.log("Executing command " + event.getMessage().getContentRaw());
        if (Utilities.INSTANCE.isCommandsChannel(event.getMember(), event.getTextChannel())) return;

        executeCommand(event);
    }

    protected abstract void executeCommand(CommandEvent event);
}
