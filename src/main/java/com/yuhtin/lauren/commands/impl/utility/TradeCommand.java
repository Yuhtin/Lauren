package com.yuhtin.lauren.commands.impl.utility;

import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

@CommandInfo(
        name = "trocar",
        type = CommandInfo.CommandType.UTILITY,
        description = "Trocar algumas coisas com outro jogador"
)
public class TradeCommand implements Command {

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) throws Exception {
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
