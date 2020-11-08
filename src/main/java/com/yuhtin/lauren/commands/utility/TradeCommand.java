package com.yuhtin.lauren.commands.utility;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import com.yuhtin.lauren.models.objects.CommonCommand;

@CommandHandler(
        name = "trocar",
        type = CommandHandler.CommandType.UTILITY,
        description = "Trocar algumas coisas com outro jogador",
        alias = {"trade", "me"}
)
public class TradeCommand extends CommonCommand {

    @Override
    protected void executeCommand(CommandEvent event) {
        // TODO
    }
}
