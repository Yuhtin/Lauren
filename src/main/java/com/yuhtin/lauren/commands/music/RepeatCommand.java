package com.yuhtin.lauren.commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.core.music.TrackManager;
import com.yuhtin.lauren.models.annotations.CommandHandler;
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
        if (!Utilities.INSTANCE.isDJ(event.getMember(), event.getTextChannel(), true)) return;

        TrackManager.get().repeat = !TrackManager.get().repeat;
        String message = TrackManager.get().repeat
                ? "<:felizpakas:742373250037710918> Parece que gosta dessa música né, vou tocar ela denovo quando acabar (e as próximas também)"
                : "<a:tchau:751941650728747140> Deixa pra lá, vou repetir mais as músicas não";
        event.getTextChannel().sendMessage(message).queue();
    }
}
