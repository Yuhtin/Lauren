package com.yuhtin.lauren.commands.impl.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.core.music.TrackManager;
import com.yuhtin.lauren.commands.CommandHandler;
import com.yuhtin.lauren.utils.helper.TrackUtils;
import com.yuhtin.lauren.utils.helper.UserUtil;

@CommandHandler(
        name = "misturar",
        type = CommandHandler.CommandType.MUSIC,
        description = "Misturar as minhas musiquinhas",
        alias = {"misture", "shuffle"}
)
public class ShuffleCommand extends Command {

    @Override
    protected void execute(CommandEvent event) {
        if (TrackUtils.get().isIdle(event.getTextChannel())) return;
        if (!UserUtil.INSTANCE.isDJ(event.getMember(), event.getTextChannel(), true)) return;

        TrackManager.of(event.getGuild()).shuffleQueue();
        event.getChannel().sendMessage("<a:infinito:703187274912759899> Misturando a lista de músicas").queue();
    }

}
