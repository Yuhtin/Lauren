package com.yuhtin.lauren.commands.impl.music;

import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import com.yuhtin.lauren.core.music.TrackManager;
import com.yuhtin.lauren.util.TrackUtils;
import lombok.val;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

@CommandInfo(
        name = "pause",
        type = CommandInfo.CommandType.MUSIC,
        description = "Pausar a música atual"
)
public class PauseCommand implements Command {

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) throws Exception {
        if (event.getGuild() == null
                || event.getMember() == null
                || TrackUtils.isIdle(event.getGuild(), hook)
                || !UserUtil.isDJ(event.getMember(), hook)) return;

        val trackManager = TrackManager.of(event.getGuild());
        trackManager.getPlayer().setPaused(!trackManager.getPlayer().isPaused());

        val message = trackManager.getPlayer().isPaused() ?
                "\uD83E\uDD7A Taxaram meu batidão, espero que me liberem logo"
                : "\uD83E\uDD73 Liberaram meu batidão uhhuuuu";

        hook.sendMessage(message).queue();
    }

}
