package com.yuhtin.lauren.commands.impl.music;

import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import com.yuhtin.lauren.commands.CommandType;
import com.yuhtin.lauren.module.Module;
import com.yuhtin.lauren.module.impl.music.MusicModule;
import com.yuhtin.lauren.module.impl.player.module.PlayerModule;
import com.yuhtin.lauren.util.MusicUtil;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

@CommandInfo(
        name = "skip.force",
        type = CommandType.MUSIC,
        description = "Forçar o pulo de uma música"
)
public class ForceSkipCommand implements Command {

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) {
        if (event.getGuild() == null || event.getMember() == null) return;

        PlayerModule playerModule = Module.instance(PlayerModule.class);
        if (playerModule == null) return;

        MusicModule musicModule = Module.instance(MusicModule.class);
        if (musicModule == null) return;

        musicModule.getByGuildId(event.getGuild()).queue(trackManager -> {
            if (MusicUtil.isIdle(trackManager, hook)) return;

            if (!MusicUtil.isMusicOwner(event.getMember(), trackManager) && !playerModule.isDJ(event.getMember())) {
                hook.sendMessage("Você não é o dono da música, então não pode pular ela <3").setEphemeral(true).queue();
                return;
            }

            trackManager.skipTrack();
            hook.sendMessage("\u23e9 Pulei a música pra você <3").queue();
        });
    }
}
