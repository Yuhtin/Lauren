package commands.admin;

import application.Lauren;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import logger.Logger;
import net.dv8tion.jda.api.Permission;
import utils.helper.Utilities;

public class RestartDatabaseCommand extends Command {

    public RestartDatabaseCommand() {
        this.name = "restartdata";
        this.aliases = new String[]{"rdatabase", "restart", "reiniciar", "restartdatabase", "databaser"};
        this.help = "Reiniciar a database";
    }

    @Override
    protected void execute(CommandEvent event) {
        if (!Utilities.isPermission(event.getMember(), event.getChannel(), Permission.ADMINISTRATOR)) return;

        Logger.log("The player " + event.getMember().getUser().getName() + " restarted my database").save();
        Lauren.data.close();
        if (!Lauren.startDatabase()) {
            event.getChannel().sendMessage("Ocorreu um erro crítico na inicialização da database, desligando bot.").queue();
            System.exit(0);
        }
        event.getChannel().sendMessage("Minha database foi reiniciada com sucesso :satisfied:").queue();
    }
}
