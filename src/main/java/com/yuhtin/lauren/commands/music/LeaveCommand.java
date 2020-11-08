package com.yuhtin.lauren.commands.music;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.core.music.TrackManager;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import com.yuhtin.lauren.models.objects.CommonCommand;
import com.yuhtin.lauren.utils.helper.Utilities;

@CommandHandler(
        name = "sair",
        type = CommandHandler.CommandType.MUSIC,
        description = "Sair do canal de voz e parar o batidão",
        alias = {"leave"}
)
public class LeaveCommand extends CommonCommand {

    @Override
    protected void executeCommand(CommandEvent event) {
        if (!Utilities.INSTANCE.isDJ(event.getMember(), event.getTextChannel(), true)) return;

        TrackManager.get().destroy();
        event.getChannel()
                .sendMessage("Que ⁉️ Pensei que estavam gostando do batidão \uD83D\uDC94 Prometo que da próxima será melhor")
                .queue();
    }
}
