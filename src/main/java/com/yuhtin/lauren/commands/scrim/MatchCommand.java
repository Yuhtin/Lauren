package com.yuhtin.lauren.commands.scrim;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.models.annotations.CommandHandler;

@CommandHandler(
        name = "partida",
        type = CommandHandler.CommandType.SCRIM,
        description = "Comando resposável pelas partidas das scrims",
        alias = {"partida", "partidas", "matches"})
public class MatchCommand extends Command {

    @Override
    protected void execute(CommandEvent event) {
    }
}
