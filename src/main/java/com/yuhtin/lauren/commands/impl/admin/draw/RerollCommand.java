package com.yuhtin.lauren.commands.impl.admin.draw;

import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import com.yuhtin.lauren.core.draw.controller.DrawController;
import com.yuhtin.lauren.utils.UserUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

@CommandInfo(
        name = "reroll",
        type = CommandInfo.CommandType.ADMIN,
        description = "Sortear um ganhador novamente"
)
public class RerollCommand implements Command {

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) throws Exception {
        if (event.getMember() == null || !UserUtil.hasPermission(event.getMember(), hook, Permission.ADMINISTRATOR)) return;

        if (DrawController.get() == null || !DrawController.get().finished) {
            hook.setEphemeral(true).sendMessage(":x: Nenhum sorteio ativo no momento.").queue();
            return;
        }

        hook.sendMessage("♻️ Eu realmente não sei o que aconteceu, mas, como mandaram, sorteando um novo vencedor").queue();
        DrawController.get().finish();
    }

}
