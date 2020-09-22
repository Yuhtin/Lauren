package com.yuhtin.lauren.commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.core.music.AudioInfo;
import com.yuhtin.lauren.core.music.TrackManager;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import com.yuhtin.lauren.utils.helper.TrackUtils;
import com.yuhtin.lauren.utils.helper.Utilities;

@CommandHandler(
        name = "repetir",
        type = CommandHandler.CommandType.MUSIC,
        description = "Ao ativar, a música atual irá se repetir 1 vez",
        alias = {"repeat"}
)
public class RepeatCommand extends Command {

    @Override
    protected void execute(CommandEvent event) {
        event.getChannel().sendMessage("<a:tchau:751941650728747140> Este comando está em manutenção até o <@272879983326658570> me consertar esse fdp").queue();
        /*

        if (TrackUtils.get().isIdle(event.getTextChannel())) return;
        if (!Utilities.INSTANCE.isDJ(event.getMember(), event.getTextChannel(), true)) return;

        AudioInfo audio = TrackManager.get().getTrackInfo();
        audio.setRepeat(!audio.isRepeat());
        audio.setRepeated(false);

        String message = audio.isRepeat()
                ? "<:felizpakas:742373250037710918> Parece que gosta dessa música né, vou tocar ela denovo quando acabar"
                : "<a:tchau:751941650728747140> Deixa pra lá, vou repetir a música mais não";
        event.getTextChannel().sendMessage(message).queue();*/
    }
}
