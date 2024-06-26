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
        name = "quit",
        type = CommandType.MUSIC,
        description = "Sair do canal de voz e parar o batidão"
)
public class LeaveCommand implements Command {

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) throws Exception {
        if (event.getGuild() == null || event.getMember() == null) return;

        PlayerModule playerModule = Module.instance(PlayerModule.class);
        if (playerModule == null) return;

        MusicModule musicModule = Module.instance(MusicModule.class);
        if (musicModule == null) return;

        musicModule.getByGuildId(event.getGuild()).queue(trackManager -> {
            if (MusicUtil.isIdle(trackManager, hook)) return;
            if (!playerModule.isDJ(event.getMember())) {
                hook.sendMessage("Você não é DJ para parar o batidão \uD83D\uDE14").setEphemeral(true).queue();
                return;
            }

            trackManager.destroy();
            hook.sendMessage("Que ⁉️ Pensei que estavam gostando do batidão " +
                    "\uD83D\uDC94 Prometo que da próxima será melhor").queue();
        });

    }

}
