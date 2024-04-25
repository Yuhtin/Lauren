package com.yuhtin.lauren.commands.impl.music;

import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import com.yuhtin.lauren.commands.CommandType;
import com.yuhtin.lauren.module.Module;
import com.yuhtin.lauren.module.impl.music.MusicModule;
import com.yuhtin.lauren.module.impl.player.module.PlayerModule;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

@CommandInfo(
        name = "queue",
        type = CommandType.MUSIC,
        description = "Ver as músicas que eu ainda vou tocar",
        args = {
                "[pagina]-Ver uma página específica da queue"
        }
)
public class QueueCommand implements Command {

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) throws Exception {
        if (event.getMember() == null || event.getGuild() == null) return;

        PlayerModule playerModule = Module.instance(PlayerModule.class);
        if (playerModule == null) return;

        MusicModule musicModule = Module.instance(MusicModule.class);
        if (musicModule == null) return;

        OptionMapping option = event.getOption("pagina");
        int page = option == null ? 1 : option.getAsInt();

        musicModule.sendQueue(page, event.getGuild(), event.getMember(), hook, null);
    }
}
