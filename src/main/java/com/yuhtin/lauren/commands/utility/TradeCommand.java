package com.yuhtin.lauren.commands.utility;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.models.annotations.CommandHandler;

@CommandHandler(
        name = "trocar",
        type = CommandHandler.CommandType.UTILITY,
        description = "Trocar algumas coisas com outro jogador",
        alias = {"trade", "me"}
)
public class TradeCommand extends Command {

    @Override
    protected void execute(CommandEvent event) {
        // TODO
    }
}
