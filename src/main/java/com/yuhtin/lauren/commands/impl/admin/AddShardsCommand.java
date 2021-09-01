package com.yuhtin.lauren.commands.impl.admin;

import com.google.inject.Inject;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.commands.CommandEvent;
import com.yuhtin.lauren.commands.CommandExecutor;
import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.commands.CommandHandler;
import com.yuhtin.lauren.utils.helper.UserUtil;
import lombok.val;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.util.concurrent.TimeUnit;

@CommandHandler(
        name = "addshard",
        type = CommandHandler.CommandType.ADMIN,
        description = "Adicionar pontos de ranked para um jogador",
        alias = {}
)
public class AddShardsCommand implements CommandExecutor {

    @Inject private PlayerController playerController;

    @Override
    public void execute(CommandEvent event) {
        if (!UserUtil.hasPermission(event.getMember(), event.getMessage(), Permission.ADMINISTRATOR, true))
            return;

        if (event.getMessage().getMentionedMembers().isEmpty()) {
            event.getChannel()
                    .sendMessage(":x: Ops, você precisa mencionar um jogador para receber os shards")
                    .delay(10, TimeUnit.SECONDS)
                    .flatMap(Message::delete)
                    .queue();
            return;
        }

        val member = event.getMessage().getMentionedMembers().get(0);
        val arguments = event.getMessage().getContentRaw().split(" ");

        if (arguments.length < 2) {
            event.getChannel().sendMessage(":x: Utilize desta forma: " + arguments[0] + " @usuario <quantidade>")
                    .delay(10, TimeUnit.SECONDS)
                    .flatMap(Message::delete)
                    .queue();
            return;
        }

        val shards = Integer.parseInt(arguments[2]);

        val data = this.playerController.get(member.getIdLong());
        data.addMoney(shards);

        event.getChannel().sendMessage("<:felizpakas:742373250037710918> " +
                "Você adicionou **" + shards + "** shards ao jogador " + member.getUser().getName())
                .queue();
    }
}
