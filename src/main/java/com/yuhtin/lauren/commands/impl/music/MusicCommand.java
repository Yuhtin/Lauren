package com.yuhtin.lauren.commands.impl.music;

import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.core.music.TrackManager;
import com.yuhtin.lauren.commands.CommandData;
import com.yuhtin.lauren.utils.TrackUtils;
import lombok.val;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

@CommandData(
        name = "musica",
        type = CommandData.CommandType.MUSIC,
        description = "Ver as informações da música atual"
)
public class MusicCommand implements Command {

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) throws Exception {
        if (event.getGuild() == null
                || event.getMember() == null
                || TrackUtils.isIdle(event.getGuild(), hook)) return;

        val trackManager = TrackManager.of(event.getGuild());
        val track = trackManager.getPlayer().getPlayingTrack();

        trackManager.tryDeleteLastMessage();

        hook.sendMessageEmbeds(TrackUtils.showTrackInfo(track, trackManager).build()).queue(messsage -> {
            trackManager.setTextChannel(messsage.getTextChannel());
            trackManager.setLastInfoMessageId(messsage.getIdLong());
        });
    }

}
