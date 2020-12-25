package com.yuhtin.lauren.commands.admin;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import com.yuhtin.lauren.utils.helper.Utilities;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;

import java.util.concurrent.TimeUnit;

@CommandHandler(
        name = "addshard",
        type = CommandHandler.CommandType.ADMIN,
        description = "Adicionar pontos de ranked para um jogador",
        alias = {}
)
public class AddShardsCommand extends Command {

    @Override
    protected void execute(CommandEvent event) {
        if (!Utilities.INSTANCE.isPermission(event.getMember(), event.getChannel(), Permission.ADMINISTRATOR, true))
            return;

        if (event.getMessage().getMentionedMembers().isEmpty()) {
            event.getChannel()
                    .sendMessage("Ops, você precisa mencionar um jogador para receber os shards")
                    .queue();
            return;
        }

        Member member = event.getMessage().getMentionedMembers().get(0);
        String[] arguments = event.getMessage().getContentRaw().split(" ");

        if (arguments.length < 2) {
            event.getChannel().sendMessage("Utilize desta forma: " + arguments[0] + " @usuario <quantidade>")
                    .queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
            return;
        }

        int shards = Integer.parseInt(arguments[2]);

        Player data = PlayerController.INSTANCE.get(member.getIdLong());
        data.addMoney(shards);

        event.getChannel().sendMessage("<:felizpakas:742373250037710918> " +
                "Você adicionou **" + shards + "** shards ao jogador " + member.getUser().getName())
                .queue();
    }
}
