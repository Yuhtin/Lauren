package com.yuhtin.lauren.commands.admin.draw;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.core.draw.controller.DrawController;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import com.yuhtin.lauren.utils.helper.Utilities;
import net.dv8tion.jda.api.Permission;

@CommandHandler(
        name = "finishdraw",
        type = CommandHandler.CommandType.ADMIN,
        description = "Finalizar um sorteio",
        alias = {"finalizar"})
public class FinishDrawCommand extends Command {

    @Override
    protected void execute(CommandEvent event) {
        if (!Utilities.INSTANCE.isPermission(event.getMember(), event.getChannel(), Permission.ADMINISTRATOR, true)) return;

        if (DrawController.get() == null || !DrawController.get().finished) {
            event.getMessage().delete().queue();
            return;
        }

        event.getChannel().sendMessage("\uD83C\uDF89 Sorteio finalizado, parabéns aos vencedores ❤️").queue();
        DrawController.delete();
    }
}
