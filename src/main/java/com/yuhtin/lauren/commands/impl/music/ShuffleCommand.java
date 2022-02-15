package com.yuhtin.lauren.commands.impl.music;

import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import com.yuhtin.lauren.core.music.TrackManager;
import com.yuhtin.lauren.utils.TrackUtils;
import com.yuhtin.lauren.utils.UserUtil;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

@CommandInfo(
        name = "shuffle",
        type = CommandInfo.CommandType.MUSIC,
        description = "Misturar as minhas musiquinhas"
)
public class ShuffleCommand implements Command {

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) throws Exception {
        if (TrackUtils.isIdle(event.getGuild(), hook)) return;
        if (!UserUtil.isDJ(event.getMember(), hook)) return;

        TrackManager.of(event.getGuild()).shuffleQueue();
        hook.sendMessage("<a:infinito:703187274912759899> Misturando a lista de músicas").queue();
    }

}
