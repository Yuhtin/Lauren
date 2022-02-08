package com.yuhtin.lauren.commands.impl.admin.draw;

import com.yuhtin.lauren.core.draw.controller.DrawController;
import com.yuhtin.lauren.commands.CommandHandler;
import com.yuhtin.lauren.utils.helper.UserUtil;
import net.dv8tion.jda.api.Permission;

@CommandHandler(
        name = "finishdraw",
        type = CommandHandler.CommandType.ADMIN,
        description = "Finalizar um sorteio",
        alias = {"finalizar"})
public class FinishDrawCommand implements CommandExecutor {

    @Override
    public void execute(CommandEvent event) {
        if (!UserUtil.hasPermission(event.getMember(), event.getMessage(), Permission.ADMINISTRATOR, true)) return;

        if (DrawController.get() == null || !DrawController.get().finished) {
            event.getMessage().delete().queue();
            return;
        }

        event.getChannel().sendMessage("\uD83C\uDF89 Sorteio finalizado, parabéns aos vencedores ❤️").queue();
        DrawController.delete();
    }
}
