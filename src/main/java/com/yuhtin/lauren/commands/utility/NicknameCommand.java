package com.yuhtin.lauren.commands.utility;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.models.annotations.CommandHandler;

@CommandHandler(
        name = "apelido",
        type = CommandHandler.CommandType.UTILITY,
        description = "Comando para alterar seu apelido",
        alias = {"nick", "nickname"}
)
public class NicknameCommand extends Command {

    @Override
    protected void execute(CommandEvent event) {
        String nick = event.getArgs().replace("[", "").replace("]", "");
        nick = "[" + PlayerController.INSTANCE.get(event.getAuthor().getIdLong()).level + "] " + nick;

        event.getChannel().sendMessage("<:feliz_pra_caralho:760202116504485948> Você modificou seu nick com sucesso").queue();
        event.getMember().modifyNickname(nick).queue();
    }
}
