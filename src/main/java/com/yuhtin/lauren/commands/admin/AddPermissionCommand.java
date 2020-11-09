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
        name = "addpermission",
        type = CommandHandler.CommandType.ADMIN,
        description = "Adicionar uma permissão a um jogador",
        alias = {"addpermissão"}
)
public class AddPermissionCommand extends Command {

    @Override
    protected void execute(CommandEvent event) {
        if (!Utilities.INSTANCE.isPermission(event.getMember(), event.getChannel(), Permission.ADMINISTRATOR, true))
            return;

        if (event.getMessage().getMentionedMembers().isEmpty()) {
            event.getChannel().sendMessage("Ops, você precisa mencionar um jogador para receber a permissão")
                    .queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
            return;
        }

        Member target = event.getMessage().getMentionedMembers().get(0);
        String[] arguments = event.getMessage().getContentRaw().split(" ");

        if (arguments.length < 2) {
            event.getChannel().sendMessage("Utilize desta forma: " + arguments[0] + " @usuario <permissão>")
                    .queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
            return;
        }

        String permission = arguments[2];

        Player data = PlayerController.INSTANCE.get(target.getIdLong());
        data.addPermission(permission);

        event.getChannel().sendMessage("<:felizpakas:742373250037710918> " +
                "Você adicionou **" + permission + "** como permissão para o jogador " + target.getUser().getName())
                .queue();
    }

}
