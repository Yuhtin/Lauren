package com.yuhtin.lauren.commands.music;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.core.music.TrackManager;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import com.yuhtin.lauren.models.objects.CommonCommand;
import com.yuhtin.lauren.utils.helper.TrackUtils;
import com.yuhtin.lauren.utils.helper.Utilities;

@CommandHandler(
        name = "fpular",
        type = CommandHandler.CommandType.MUSIC,
        description = "Forçar o pulo de uma música",
        alias = {"forcepular", "fp"}
)
public class ForceJumpCommand extends CommonCommand {

    @Override
    protected void executeCommand(CommandEvent event) {

        if (TrackUtils.get().isIdle(event.getTextChannel())) return;
        if (!Utilities.INSTANCE.isDJ(event.getMember(), event.getTextChannel(), true)) return;

        TrackManager.get().player.stopTrack();
        event.getChannel().sendMessage("\u23e9 Pulei a música pra você <3").queue();
    }
}
