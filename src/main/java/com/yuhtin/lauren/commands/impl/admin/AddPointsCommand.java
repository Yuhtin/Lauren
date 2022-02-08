package com.yuhtin.lauren.commands.impl.admin;

import com.google.inject.Inject;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.commands.CommandHandler;
import com.yuhtin.lauren.utils.helper.UserUtil;
import lombok.val;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;

import java.util.concurrent.TimeUnit;

@CommandHandler(
        name = "addpontos",
        type = CommandHandler.CommandType.ADMIN,
        description = "Adicionar pontos de ranked para um jogador",
        alias = {"adicionarpontos"})
public class AddPointsCommand implements CommandExecutor {

    @Inject private PlayerController playerController;

    @Override
    public void execute(CommandEvent event) {
        if (!UserUtil.hasPermission(event.getMember(), event.getMessage(), Permission.ADMINISTRATOR, true))
            return;

        if (event.getMessage().getMentionedMembers().isEmpty()) {
            event.getChannel()
                    .sendMessage("Ops, você precisa mencionar um jogador para receber os pontos")
                    .queue();
            return;
        }

        val member = event.getMessage().getMentionedMembers().get(0);
        val arguments = event.getMessage().getContentRaw().split(" ");

        if (arguments.length < 2) {
            event.getChannel().sendMessage(":x: Utilize desta forma: " + arguments[0] + " @Usuario <quantidade>")
                    .delay(10, TimeUnit.SECONDS)
                    .flatMap(Message::delete)
                    .queue();
            return;
        }

        val xp = Integer.parseInt(arguments[2]);

        val data = playerController.get(member.getIdLong());
        data.setRankedPoints(data.getRankedPoints() + xp);

        data.updateRank();
        event.getChannel().sendMessage("<:felizpakas:742373250037710918> " +
                "Você adicionou **" + xp + "** pontos de patente ao jogador " + member.getUser().getName())
                .queue();
    }
}
