package com.yuhtin.lauren.commands.impl.music;

import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import com.yuhtin.lauren.util.MusicUtil;
import lombok.val;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

@CommandInfo(
        name = "playing",
        type = CommandInfo.CommandType.MUSIC,
        description = "Ver as informações da música atual"
)
public class MusicCommand implements Command {

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) throws Exception {
        if (event.getGuild() == null
                || event.getMember() == null
                || MusicUtil.isIdle(event.getGuild(), hook)) return;

        val trackManager = TrackManager.getByGuild(event.getGuild());
        val track = trackManager.getPlayer().getPlayingTrack();

        trackManager.tryDeleteLastMessage();

        hook.sendMessageEmbeds(MusicUtil.showTrackInfo(track, trackManager).build()).queue(messsage -> {
            trackManager.setTextChannel(messsage.getTextChannel());
            trackManager.setLastInfoMessageId(messsage.getIdLong());
        });
    }

}
