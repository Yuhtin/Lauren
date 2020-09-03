package com.yuhtin.lauren.commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.core.music.TrackManager;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import com.yuhtin.lauren.utils.helper.TrackUtils;
import com.yuhtin.lauren.utils.helper.Utilities;

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
        if (!Utilities.INSTANCE.isDJ(event.getMember(), event.getTextChannel(), true)) return;

        TrackManager.get().player.setPaused(!TrackManager.get().player.isPaused());
        String message = TrackManager.get().player.isPaused() ?
                "\uD83E\uDD7A Taxaram meu batidão, espero que me liberem logo"
                : "\uD83E\uDD73 Liberaram meu batidão uhhuuuu";
        event.getChannel().sendMessage(message).queue();
    }
}
