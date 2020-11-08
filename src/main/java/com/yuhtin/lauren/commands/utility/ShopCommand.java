package com.yuhtin.lauren.commands.utility;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.models.annotations.CommandHandler;

@CommandHandler(
        name = "loja",
        type = CommandHandler.CommandType.UTILITY,
        description = "Ver algumas coisinhas que tou vendendo",
        alias = {"shop"}
)
public class ShopCommand extends Command {

    @Override
    protected void execute(CommandEvent event) {
        // TODO
    }
}
