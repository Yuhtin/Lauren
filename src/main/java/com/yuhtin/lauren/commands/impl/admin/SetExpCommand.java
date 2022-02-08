package com.yuhtin.lauren.commands.impl.admin;

import com.google.inject.Inject;
import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandData;
import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.utils.helper.UserUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;

import java.util.concurrent.TimeUnit;

@CommandData(
        name = "setarxp",
        type = CommandData.CommandType.CONFIG,
        description = "Abusar uns xpzinhos pros ademiros",
        alias = {"setxp"})
public class SetExpCommand implements Command {

    @Inject private PlayerController playerController;

    @Override
    public void execute(CommandEvent event) {
        if (!UserUtil.hasPermission(event.getMember(), event.getMessage(), Permission.ADMINISTRATOR, true))
            return;

        if (event.getMessage().getMentionedMembers().isEmpty()) {
            event.getChannel().sendMessage("Ops, você precisa mencionar um jogador para receber o xp")
                    .queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
            return;
        }

        Member member = event.getMessage().getMentionedMembers().get(0);
        String[] arguments = event.getMessage().getContentRaw().split(" ");

        if (arguments.length < 2) {
            event.getChannel().sendMessage("Utilize desta forma: " + arguments[0] + " @usuario <xp>")
                    .queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
            return;
        }

        Player player = this.playerController.get(member.getIdLong());
        player.gainXP(Integer.parseInt(arguments[2]));
        event.getChannel().sendMessage("Consegui setar com sucesso o XP do usuário inserido")
                .queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
    }
}
