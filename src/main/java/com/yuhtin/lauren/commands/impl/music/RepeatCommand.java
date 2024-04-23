package com.yuhtin.lauren.commands.impl.music;

import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import com.yuhtin.lauren.commands.CommandType;
import com.yuhtin.lauren.module.Module;
import com.yuhtin.lauren.module.impl.music.AudioInfo;
import com.yuhtin.lauren.module.impl.music.MusicModule;
import com.yuhtin.lauren.module.impl.player.module.PlayerModule;
import com.yuhtin.lauren.util.MusicUtil;
import lombok.val;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

@CommandInfo(
        name = "loop",
        type = CommandType.MUSIC,
        description = "Ao ativar, a música atual irá se repetir 1 vez"
)
public class RepeatCommand implements Command {

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

            AudioInfo audioInfo = trackManager.getTrackInfo();
            if (audioInfo == null) return;

            audioInfo.setRepeat(!audioInfo.isRepeat());

            val message = audioInfo.isRepeat()
                    ? "<:felizpakas:742373250037710918> Parece que gosta dessa música né, vou tocar ela denovo quando acabar"
                    : "<a:tchau:751941650728747140> Deixa pra lá, vou repetir a música mais não";

            hook.sendMessage(message).queue();
        });
    }
}
