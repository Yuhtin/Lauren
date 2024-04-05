package com.yuhtin.lauren.commands.impl.music;

import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import com.yuhtin.lauren.core.music.TrackManager;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

@CommandInfo(
        name = "quit",
        type = CommandInfo.CommandType.MUSIC,
        description = "Sair do canal de voz e parar o batidão"
)
public class LeaveCommand implements Command {

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) throws Exception {
        if (event.getGuild() == null || event.getMember() == null || !UserUtil.isDJ(event.getMember(), hook)) return;

        TrackManager.of(event.getGuild()).destroy();
        hook.sendMessage("Que ⁉️ Pensei que estavam gostando do batidão " +
                "\uD83D\uDC94 Prometo que da próxima será melhor").queue();
    }

}
