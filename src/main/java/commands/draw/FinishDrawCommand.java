package commands.draw;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import draw.Draw;
import draw.controller.DrawController;
import models.annotations.CommandHandler;
import net.dv8tion.jda.api.Permission;
import utils.helper.Utilities;

@CommandHandler(name = "finishdraw", type = CommandHandler.CommandType.SUPORT, description = "Finalizar um sorteio")
public class FinishDrawCommand extends Command {

    public FinishDrawCommand() {
        this.name = "finishdraw";
    }

    @Override
    protected void execute(CommandEvent event) {
        if (!Utilities.isPermission(event.getMember(), event.getChannel(), Permission.ADMINISTRATOR)) return;

        if (DrawController.get() == null || !DrawController.get().finished) {
            event.getMessage().delete().queue();
            return;
        }

        event.getChannel().sendMessage("\uD83C\uDF89 Sorteio finalizado, parabéns aos vencedores ❤️").queue();
        DrawController.delete();
    }
}
