package com.yuhtin.lauren.commands.impl.music;

import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandData;
import com.yuhtin.lauren.core.music.TrackManager;
import com.yuhtin.lauren.utils.helper.TrackUtils;
import com.yuhtin.lauren.utils.helper.UserUtil;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

@CommandData(
        name = "fpular",
        type = CommandData.CommandType.MUSIC,
        description = "Forçar o pulo de uma música",
        args = {}
)
public class ForceJumpCommand implements Command {

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) {
        if (event.getGuild() == null
                || event.getMember() == null
                || TrackUtils.get().isIdle(event.getGuild(), hook)
                || !UserUtil.isDJ(event.getMember(), hook)) return;

        TrackManager.of(event.getGuild()).getPlayer().stopTrack();
        event.getChannel().sendMessage("\u23e9 Pulei a música pra você <3").queue();
    }
}
