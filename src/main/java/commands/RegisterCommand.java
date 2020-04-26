package commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.Permission;
import utils.Utilities;

public class RegisterCommand extends Command {
    public RegisterCommand() {
        this.name = "createregister";
        this.help = "Criar a mensagem de registro";
    }
    @Override
    protected void execute(CommandEvent event) {
        event.getMessage().delete().queue();

       if (Utilities.isPermissionCheck(event.getMember(), event.getChannel(), Permission.ADMINISTRATOR)) return;

    }
}
