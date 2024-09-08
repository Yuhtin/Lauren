package com.yuhtin.lauren.commands.impl.music;

import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import com.yuhtin.lauren.commands.CommandType;
import com.yuhtin.lauren.module.Module;
import com.yuhtin.lauren.module.impl.music.MusicModule;
import com.yuhtin.lauren.module.impl.music.MusicSearchType;
import com.yuhtin.lauren.module.impl.player.module.PlayerModule;
import com.yuhtin.lauren.util.LoggerUtil;
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
            try {
                val channel = event.getMember().getVoiceState().getChannel();

                if (trackManager.getAudioChannel() != null && !trackManager.getAudioChannel().equals(channel)) {
                    LoggerUtil.getLogger().info("The player " + event.getMember().getUser().getName() + " tried to play a music in another channel");
                    if (channel == null || channel.getIdLong() != trackManager.getAudioChannel().getIdLong()) {
                        hook.sendMessage("\uD83C\uDFB6 Você precisa estar no mesmo canal de voz que eu para tocar música!").queue();
                        return;
                    }
                }

                var input = event.getOption("musica").getAsString();
                input = input.contains("http") ? input : "spsearch: " + input;

                LoggerUtil.getLogger().info("The player " + event.getMember().getUser().getName() + " added a music to queue");
                musicModule.loadTrack(MusicSearchType.SIMPLE_SEARCH, input, event.getMember(), hook, null);
            } catch (Exception exception) {
                LoggerUtil.getLogger().severe("An error occurred while trying to play a music");
                LoggerUtil.printException(exception);
                hook.sendMessage("\uD83D\uDEAB Ocorreu um erro ao tentar tocar a música!").queue();
            }
        });
    }

}
