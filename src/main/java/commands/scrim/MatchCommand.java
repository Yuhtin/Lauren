package commands.scrim;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class MatchCommand extends Command {

    public MatchCommand() {
        this.name = "match";
        this.aliases = new String[]{"partida", "partidas", "matches"};
        this.help = "Comando de partidas";
    }

    @Override
    protected void execute(CommandEvent event) {
        e
    }
}
