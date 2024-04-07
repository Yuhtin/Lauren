package com.yuhtin.lauren.commands.impl.admin.draw;

import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import com.yuhtin.lauren.core.draw.controller.DrawController;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

@CommandInfo(
        name = "finishdraw",
        type = CommandType.ADMIN,
        description = "Finalizar um sorteio"
)
public class FinishDrawCommand implements Command {
    @Override
    public void execute(CommandInteraction event, InteractionHook hook) throws Exception {
        if (event.getMember() == null
                || !UserUtil.hasPermission(event.getMember(), hook, Permission.ADMINISTRATOR)) return;

        if (DrawController.get() == null || !DrawController.get().finished) {
            hook.setEphemeral(true).sendMessage(":x: Nenhum sorteio ativo no momento.").queue();
            return;
        }

        hook.sendMessage("\uD83C\uDF89 Sorteio finalizado, parabéns aos vencedores ❤️").queue();
        DrawController.delete();
    }

}
