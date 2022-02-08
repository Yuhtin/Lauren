package com.yuhtin.lauren.commands.impl.admin;

import com.google.inject.Inject;
import com.yuhtin.lauren.commands.CommandHandler;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.utils.helper.UserUtil;
import lombok.val;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;

import java.util.concurrent.TimeUnit;

@CommandHandler(
        name = "addpermission",
        type = CommandHandler.CommandType.ADMIN,
        description = "Adicionar uma permissão a um jogador",
        alias = {"addpermissão"}
)
public class AddPermissionCommand implements CommandExecutor {

    @Inject private PlayerController playerController;

    @Override
    public void execute(CommandEvent event) {
        if (!UserUtil.hasPermission(event.getMember(), event.getMessage(), Permission.ADMINISTRATOR, true))
            return;

        if (event.getMessage().getMentionedMembers().isEmpty()) {
            event.getChannel().sendMessage(":x: Ops, você precisa mencionar um jogador para receber a permissão")
                    .delay(10, TimeUnit.SECONDS)
                    .flatMap(Message::delete)
                    .queue();
            return;
        }

        val target = event.getMessage().getMentionedMembers().get(0);
        val arguments = event.getMessage().getContentRaw().split(" ");

        if (arguments.length < 3) {
            event.getChannel()
                    .sendMessage(":x: Utilize desta forma: " + arguments[0] + " @usuario <permissão>")
                    .queue();
            return;
        }

        val permission = arguments[2];

        val data = playerController.get(target.getIdLong());
        data.addPermission(permission);

        event.getChannel().sendMessage("<:felizpakas:742373250037710918> " +
                "Você adicionou **" + permission + "** como permissão para o jogador " + target.getUser().getName())
                .queue();
    }

}
