package com.yuhtin.lauren.commands.admin;

import com.yuhtin.lauren.application.Lauren;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.models.enums.LogType;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import com.yuhtin.lauren.utils.helper.TaskHelper;
import com.yuhtin.lauren.utils.helper.Utilities;

import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

@CommandHandler(name = "restart", type = CommandHandler.CommandType.CONFIG, description = "Reiniciar meus sistemas :d")
public class RestartCommand extends Command {

    public RestartCommand() {
        this.name = "restart";
        this.aliases = new String[]{"reiniciar"};
    }

    @Override
    protected void execute(CommandEvent event) {
        if (!Utilities.isOwner(event.getChannel(), event.getMember().getUser(), true)) return;

        event.getChannel().sendMessage("Reiniciando meus sistemas :satisfied:").queue();
        Logger.log("The player " + event.getMember().getUser().getName() + " restarting my systems", LogType.LOG).save();

        TaskHelper.schedule(new TimerTask() {
            @Override
            public void run() {
                Lauren.finish();
            }
        }, 3, TimeUnit.SECONDS);
    }
}
