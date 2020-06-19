package com.yuhtin.lauren.commands.admin;

import com.yuhtin.lauren.application.Lauren;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import net.dv8tion.jda.api.Permission;
import com.yuhtin.lauren.utils.helper.Utilities;

@CommandHandler(name = "restartdata", type = CommandHandler.CommandType.CONFIG, description = "Reiniciar a minha database :D")
public class RestartDatabaseCommand extends Command {

    public RestartDatabaseCommand() {
        this.name = "restartdata";
        this.aliases = new String[]{"rdatabase", "restart", "reiniciar", "restartdatabase", "databaser"};
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
