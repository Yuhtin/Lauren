package com.yuhtin.lauren.commands.impl.music;

import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import com.yuhtin.lauren.core.music.TrackManager;
import lombok.val;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

@CommandInfo(
        name = "play",
        type = CommandInfo.CommandType.MUSIC,
        description = "Tocar algum somzinho ai",
        args = {
                "<musica>-Link ou nome de uma música ou vídeo"
        }
)
public class PlayCommand implements Command {

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) throws Exception {
        if (event.getMember() == null || event.getGuild() == null) return;

        val channel = event.getMember().getVoiceState().getChannel();
        val trackManager = TrackManager.of(event.getGuild());

        if (trackManager.getAudio() != null && !trackManager.getAudio().equals(channel)) {
            var lauren = false;
            for (val member : trackManager.getAudio().getMembers()) {
                if (member.getUser().getId().equals(event.getJDA().getSelfUser().getId())) {
                    lauren = true;
                    break;
                }
            }

            if (lauren && !UserUtil.isDJ(event.getMember(), null)) {
                hook.sendMessage("\uD83C\uDFB6 Você precisa estar no mesmo canal que eu para usar isto").queue();
                return;
            }
        }

        var input = event.getOption("musica").getAsString();
        input = input.contains("http") ? input : "ytsearch: " + input;

        trackManager.loadTrack(input, event.getMember(), hook, TrackManager.SearchType.SIMPLE_SEARCH);
        trackManager.setTextChannel(event.getTextChannel());
    }

}
