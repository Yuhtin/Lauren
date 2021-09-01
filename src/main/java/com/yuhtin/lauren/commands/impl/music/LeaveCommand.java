package com.yuhtin.lauren.commands.impl.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.core.music.TrackManager;
import com.yuhtin.lauren.commands.CommandHandler;
import com.yuhtin.lauren.utils.helper.Utilities;

@CommandHandler(
        name = "sair",
        type = CommandHandler.CommandType.MUSIC,
        description = "Sair do canal de voz e parar o batidão",
        alias = {"leave", "quit"}
)
public class LeaveCommand extends Command {

    @Override
    protected void execute(CommandEvent event) {
        if (!Utilities.INSTANCE.isDJ(event.getMember(), event.getTextChannel(), true)) return;

        TrackManager.of(event.getGuild()).destroy();
        event.getChannel()
                .sendMessage("Que ⁉️ Pensei que estavam gostando do batidão \uD83D\uDC94 Prometo que da próxima será melhor")
                .queue();
    }
}
