package com.yuhtin.lauren.commands.admin;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.utils.helper.Utilities;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;

import java.util.concurrent.TimeUnit;

@CommandHandler(
        name = "setarxp",
        type = CommandHandler.CommandType.CONFIG,
        description = "Abusar uns xpzinhos pros ademiros",
        alias = {"setxp"})
public class SetExpCommand extends Command {

    @Override
    protected void execute(CommandEvent event) {
        if (!Utilities.INSTANCE.isPermission(event.getMember(), event.getChannel(), Permission.ADMINISTRATOR, true)) return;

        if (event.getMessage().getMentionedMembers().size() < 1) {
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

        Player player = PlayerController.INSTANCE.get(member.getIdLong());
        player.experience = Integer.parseInt(arguments[2]);
        event.getChannel().sendMessage("Consegui setar com sucesso o XP do usuário inserido")
                .queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
    }
}
