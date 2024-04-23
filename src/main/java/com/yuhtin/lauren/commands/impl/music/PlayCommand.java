package com.yuhtin.lauren.commands.impl.music;

import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import com.yuhtin.lauren.commands.CommandType;
import com.yuhtin.lauren.module.Module;
import com.yuhtin.lauren.module.impl.music.MusicModule;
import com.yuhtin.lauren.module.impl.music.MusicSearchType;
import com.yuhtin.lauren.module.impl.player.module.PlayerModule;
import lombok.val;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

@CommandInfo(
        name = "play",
        type = CommandType.MUSIC,
        description = "Tocar algum somzinho ai",
        args = {
                "<musica>-Link ou nome de uma música ou vídeo"
        }
)
public class PlayCommand implements Command {

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) throws Exception {
        if (event.getMember() == null || event.getGuild() == null) return;

        PlayerModule playerModule = Module.instance(PlayerModule.class);
        if (playerModule == null) return;

        MusicModule musicModule = Module.instance(MusicModule.class);
        if (musicModule == null) return;

        musicModule.getByGuildId(event.getGuild()).queue(trackManager -> {
            val channel = event.getMember().getVoiceState().getChannel();

            if (trackManager.getAudioChannel() != null && !trackManager.getAudioChannel().equals(channel)) {
                var lauren = false;
                for (val member : trackManager.getAudioChannel().getMembers()) {
                    if (member.getUser().getId().equals(event.getJDA().getSelfUser().getId())) {
                        lauren = true;
                        break;
                    }
                }

                if (lauren && !playerModule.isDJ(event.getMember())) {
                    hook.sendMessage("\uD83C\uDFB6 Você precisa estar no mesmo canal que eu para usar isto").queue();
                    return;
                }
            }

            var input = event.getOption("musica").getAsString();
            input = input.contains("http") ? input : "ytsearch: " + input;

            musicModule.loadTrack(input, event.getMember(), hook, MusicSearchType.SIMPLE_SEARCH);
        });
    }

}
