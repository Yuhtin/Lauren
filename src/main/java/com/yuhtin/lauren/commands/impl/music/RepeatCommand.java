package com.yuhtin.lauren.commands.impl.music;

import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandData;
import com.yuhtin.lauren.core.music.AudioInfo;
import com.yuhtin.lauren.core.music.TrackManager;
import com.yuhtin.lauren.utils.TrackUtils;
import com.yuhtin.lauren.utils.UserUtil;
import lombok.val;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

@CommandData(
        name = "repetir",
        type = CommandData.CommandType.MUSIC,
        description = "Ao ativar, a música atual irá se repetir 1 vez"
)
public class RepeatCommand implements Command {

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) throws Exception {
        if (event.getGuild() == null
                || event.getMember() == null
                || TrackUtils.isIdle(event.getGuild(), hook)
                || !UserUtil.isDJ(event.getMember(), hook)) return;

        val audio = TrackManager.of(event.getGuild()).getTrackInfo();
        audio.setRepeat(!audio.isRepeat());

        val message = audio.isRepeat()
                ? "<:felizpakas:742373250037710918> Parece que gosta dessa música né, vou tocar ela denovo quando acabar"
                : "<a:tchau:751941650728747140> Deixa pra lá, vou repetir a música mais não";
        event.getTextChannel().sendMessage(message).queue();
    }
}
