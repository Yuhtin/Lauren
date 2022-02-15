package com.yuhtin.lauren.commands.impl.music;

import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import com.yuhtin.lauren.core.music.TrackManager;
import com.yuhtin.lauren.utils.TrackUtils;
import lombok.val;
import lombok.var;
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

        if (!TrackUtils.isInMusicChannel(event.getMember())) {
            hook.sendMessage(
                    "\uD83C\uDFB6 Amiguinho, entre no canal `\uD83C\uDFB6┇Batidões` para poder usar comando de música"
            ).queue();
            return;
        }

        val trackManager = TrackManager.of(event.getGuild());
        if (trackManager.getAudio() != null && !trackManager.getAudio().equals(event.getMember().getVoiceState().getChannel())) {
            hook.sendMessage("\uD83C\uDFB6 Você precisa estar no mesmo canal que eu para usar isto").queue();
            return;
        }

        var input = event.getOption("musica").getAsString();
        input = input.contains("http") ? input : "ytsearch: " + input;

        if (input.toLowerCase().contains("som de")
                || input.toLowerCase().contains("som do")
                || input.toLowerCase().contains("som da")) {
            hook.sendMessage("<a:nao:704295026036834375> Nem fodendo, hoje não vai rolar").queue();
            return;
        }

        trackManager.loadTrack(input, event.getMember(), hook, TrackManager.SearchType.SIMPLE_SEARCH);
        trackManager.setTextChannel(event.getTextChannel());
    }

}
