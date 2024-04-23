package com.yuhtin.lauren.commands.impl.music;

import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import com.yuhtin.lauren.commands.CommandType;
import com.yuhtin.lauren.module.Module;
import com.yuhtin.lauren.module.impl.music.MusicModule;
import com.yuhtin.lauren.module.impl.player.module.PlayerModule;
import com.yuhtin.lauren.util.LoggerUtil;
import com.yuhtin.lauren.util.MusicUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.File;

@CommandInfo(
        name = "download",
        type = CommandType.MUSIC,
        description = "Fazer download de todo o áudio que eu ouvi",
        permissions = { Permission.ADMINISTRATOR }
)
public class DownloadAudioCommand implements Command {

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) {
        if (event.getGuild() == null || event.getMember() == null) return;

        PlayerModule playerModule = Module.instance(PlayerModule.class);
        if (playerModule == null) return;

        MusicModule musicModule = Module.instance(MusicModule.class);
        if (musicModule == null) return;

        musicModule.getByGuildId(event.getGuild()).queue(trackManager -> trackManager.downloadAudio()
                .queue(file -> {
                    if (file == null) {
                        hook.sendMessage("\u26a0 Não encontrei nenhum áudio para baixar").queue();
                        return;
                    }

                    hook.sendMessage("\u23e9 Fiz o download do áudio pra você <3")
                            .addFiles(FileUpload.fromData(file))
                            .queue(message -> file.delete());
                }));

    }
}
