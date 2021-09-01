package com.yuhtin.lauren.commands.impl.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.core.music.TrackManager;
import com.yuhtin.lauren.commands.CommandHandler;
import com.yuhtin.lauren.utils.helper.TrackUtils;
import com.yuhtin.lauren.utils.helper.UserUtil;

@CommandHandler(
        name = "pausar",
        type = CommandHandler.CommandType.MUSIC,
        description = "Pausar a música atual",
        alias = {"pause"}
)
public class PauseCommand extends Command {

    @Override
    protected void execute(CommandEvent event) {
        if (TrackUtils.get().isIdle(event.getTextChannel())) return;
        if (!UserUtil.INSTANCE.isDJ(event.getMember(), event.getTextChannel(), true)) return;

        TrackManager trackManager = TrackManager.of(event.getGuild());
        trackManager.getPlayer().setPaused(!trackManager.getPlayer().isPaused());

        String message = trackManager.getPlayer().isPaused() ?
                "\uD83E\uDD7A Taxaram meu batidão, espero que me liberem logo"
                : "\uD83E\uDD73 Liberaram meu batidão uhhuuuu";
        event.getChannel().sendMessage(message).queue();
    }
}
