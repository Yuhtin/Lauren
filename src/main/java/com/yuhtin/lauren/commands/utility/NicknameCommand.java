package com.yuhtin.lauren.commands.utility;

import com.google.inject.Inject;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.models.annotations.CommandHandler;

@CommandHandler(
        name = "apelido",
        type = CommandHandler.CommandType.UTILITY,
        description = "Comando para alterar seu apelido",
        alias = {"nick", "nickname"}
)
public class NicknameCommand extends Command {

    @Inject private PlayerController playerController;

    @Override
    protected void execute(CommandEvent event) {

        Player player = this.playerController.get(event.getAuthor().getIdLong());
        if (!player.hasPermission("commands.nickname")) {

            event.getChannel().sendMessage("<:oi:762303876732420176> " +
                    "Você não tem permissão para usar este comando, compre-a em `$loja`.").queue();
            return;

        }

        if (event.getArgs().equalsIgnoreCase("")) {
            event.getChannel().sendMessage("<:oi:762303876732420176> Acho que ta faltando inserir o nick nekkkkkk").queue();
            return;
        }

        String nick = event.getArgs().replace("[", "").replace("]", "");
        nick = "[" + player.getLevel() + "] " + nick;

        if (nick.length() > 32) {
            event.getChannel().sendMessage("<:chorano:726207542413230142> O nick escolhido é muito grande").queue();
            return;
        }

        event.getChannel().sendMessage("<:feliz_pra_caralho:760202116504485948> Você modificou seu nick com sucesso (os parênteses fazem parte do sistema de nível).").queue();
        event.getMember().modifyNickname(nick).queue();
    }
}
