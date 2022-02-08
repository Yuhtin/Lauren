package com.yuhtin.lauren.commands.impl.admin.draw;

import com.yuhtin.lauren.core.draw.controller.DrawController;
import com.yuhtin.lauren.commands.CommandHandler;
import com.yuhtin.lauren.utils.helper.UserUtil;
import net.dv8tion.jda.api.Permission;

@CommandHandler(
        name = "reroll",
        type = CommandHandler.CommandType.ADMIN,
        description = "Sortear um ganhador novamente",
        alias = {"resortear", "redraw"})
public class RerollCommand implements CommandExecutor {

    @Override
    public void execute(CommandEvent event) {
        if (!UserUtil.hasPermission(event.getMember(), event.getMessage(), Permission.ADMINISTRATOR, true)) return;

        if (DrawController.get() == null || !DrawController.get().finished) {
            event.getMessage().delete().queue();
            return;
        }

        event.getChannel().sendMessage("♻️ Eu realmente não sei o que aconteceu, mas, como mandaram, sorteando um novo vencedor").queue();
        DrawController.get().finish();
    }
}
