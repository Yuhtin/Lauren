package com.yuhtin.lauren.commands.impl.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.core.music.TrackManager;
import com.yuhtin.lauren.commands.CommandHandler;
import com.yuhtin.lauren.utils.helper.TrackUtils;
import com.yuhtin.lauren.utils.helper.UserUtil;

@CommandHandler(
        name = "fpular",
        type = CommandHandler.CommandType.MUSIC,
        description = "Forçar o pulo de uma música",
        alias = {"forcepular", "fp"}
)
public class ForceJumpCommand extends Command {

    @Override
    protected void execute(CommandEvent event) {
        if (TrackUtils.get().isIdle(event.getTextChannel())) return;
        if (!UserUtil.INSTANCE.isDJ(event.getMember(), event.getTextChannel(), true)) return;

        TrackManager.of(event.getGuild()).getPlayer().stopTrack();
        event.getChannel().sendMessage("\u23e9 Pulei a música pra você <3").queue();
    }
}
