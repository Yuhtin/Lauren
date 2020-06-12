package commands.admin;

import application.Lauren;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.Permission;
import utils.helper.Utilities;

public class RestartDatabaseCommand extends Command {

    public RestartDatabaseCommand() {
        this.name = "databaser";
        this.aliases = new String[]{"restart"};
        this.help = "Reiniciar a database";
    }

    @Override
    protected void execute(CommandEvent event) {
        if (!Utilities.isPermission(event.getMember(), event.getChannel(), Permission.ADMINISTRATOR)) return;

        Lauren.data.close();
        if (!Lauren.startDatabase()) {
            event.getChannel().sendMessage("Ocorreu um erro crítico na inicialização da database, desligando bot.").queue();
            System.exit(0);
        }
        event.getChannel().sendMessage("Minha database foi reiniciada com sucesso :satisfied:").queue();
    }
}
