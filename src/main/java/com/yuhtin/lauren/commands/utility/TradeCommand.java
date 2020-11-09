package com.yuhtin.lauren.commands.utility;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.models.annotations.CommandHandler;

@CommandHandler(
        name = "trocar",
        type = CommandHandler.CommandType.UTILITY,
        description = "Trocar algumas coisas com outro jogador",
        alias = {"trade", "me"}
)
public class TradeCommand extends Command {

    @Override
    protected void execute(CommandEvent event) {

        /*Player player = PlayerController.INSTANCE.get(event.getAuthor().getIdLong());
        if (!player.hasPermission("commands.trade")) {

            event.getChannel().sendMessage("<:oi:762303876732420176> " +
                    "Você não tem permissão para usar este comando, compre-a em `$loja`.").queue();
            return;

        }

        if (event.getMessage().getMentionedMembers().isEmpty()) {

            event.getChannel()
                    .sendMessage("Formato: +trade @usuario <coisas pra trocar>\n" +
                            "Para trocar várias coisas junto, separe-as com $\n\n" +
                            "Exemplo: **+trade @Yuhtin 1box$100shard$1key\n" +
                            "**Então**, o outro usuário deverá digitar o que quer trocar por **1 lootbox** + **100 shard** + **1 chave**")
                    .queue();
            return;

        }

        event.getChannel().sendMessage("<@" + event.getAuthor().getId() + ">, escreva o que quer trocar").queue();*/
        // TODO

    }
}
