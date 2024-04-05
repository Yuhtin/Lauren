package com.yuhtin.lauren.commands.impl.music;

import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import com.yuhtin.lauren.util.MusicUtil;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

import java.io.File;

@CommandInfo(
        name = "download",
        type = CommandInfo.CommandType.MUSIC,
        description = "Fazer download de todo o áudio que eu ouvi"
)
public class DownloadAudioCommand implements Command {

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) {
        if (event.getGuild() == null
                || event.getMember() == null
                || MusicUtil.isIdle(event.getGuild(), hook)
                || !UserUtil.isDJ(event.getMember(), hook)) return;

        File file = TrackManager.getByGuild(event.getGuild()).downloadAudio();
        hook.sendMessage("\u23e9 Fiz o download do áudio pra você <3")
                .addFile(file)
                .queue(message -> file.delete());
    }
}
