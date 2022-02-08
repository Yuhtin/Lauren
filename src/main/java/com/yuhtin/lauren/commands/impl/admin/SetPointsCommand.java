package com.yuhtin.lauren.commands.impl.admin;

import com.google.inject.Inject;
import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.commands.CommandHandler;
import com.yuhtin.lauren.utils.helper.UserUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;

import java.util.concurrent.TimeUnit;

@CommandHandler(
        name = "setarpontos",
        type = CommandHandler.CommandType.ADMIN,
        description = "Setar os pontos de ranked para um jogador",
        alias = {"setpoints"})
public class SetPointsCommand implements CommandExecutor {

    @Inject private PlayerController playerController;

    @Override
    public void execute(CommandEvent event) {
        if (!UserUtil.hasPermission(event.getMember(), event.getMessage(), Permission.ADMINISTRATOR, true))
            return;

        if (event.getMessage().getMentionedMembers().isEmpty()) {
            event.getChannel().sendMessage("Ops, você precisa mencionar um jogador para receber os pontos")
                    .queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
            return;
        }

        Member member = event.getMessage().getMentionedMembers().get(0);
        String[] arguments = event.getMessage().getContentRaw().split(" ");

        if (arguments.length < 3) {
            event.getChannel().sendMessage("Utilize desta forma: " + arguments[0] + " @Usuario <quantidade>")
                    .queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
            return;
        }

        int xp = Integer.parseInt(arguments[2]);
        Player data = this.playerController.get(member.getIdLong());
        data.setRankedPoints(xp);

        data.updateRank();
        event.getChannel().sendMessage("<:felizpakas:742373250037710918> " +
                "Você setou **" + xp + "** pontos de patente ao jogador " + member.getUser().getName())
                .queue();
    }
}
