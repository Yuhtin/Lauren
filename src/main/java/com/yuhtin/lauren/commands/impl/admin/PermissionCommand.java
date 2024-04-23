package com.yuhtin.lauren.commands.impl.admin;

import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import com.yuhtin.lauren.commands.CommandType;
import com.yuhtin.lauren.module.Module;
import com.yuhtin.lauren.module.impl.player.module.PlayerModule;
import lombok.val;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

@CommandInfo(
        name = "permission",
        type = CommandType.ADMIN,
        description = "Alterar uma permissão em um jogador.",
        args = {
                "<option>-Use add para adicionar ou remove para remover.",
                "<@player>-Jogador que deseja fazer a alteração.",
                "<permission>-Permissão que deseja adicionar ou remover."
        },
        permissions = { Permission.ADMINISTRATOR }
)
public class PermissionCommand implements Command {

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) throws Exception {
        val option = event.getOption("option").getAsString();
        val user = event.getOption("player").getAsUser();
        val permission = event.getOption("permission").getAsString();

        Module.instance(PlayerModule.class)
                .retrieve(user.getIdLong())
                .thenAccept(player -> {
                    if (option.equalsIgnoreCase("add")) {
                        player.addPermission(permission);

                        hook.setEphemeral(true).sendMessage(
                                "<:felizpakas:742373250037710918> " +
                                        "Você adicionou a permissão **" + permission + "** para o jogador " + user.getName()
                        ).queue();
                        return;
                    } else if (option.equalsIgnoreCase("remove")) {
                        player.removePermission(permission);

                        hook.setEphemeral(true).sendMessage(
                                "<:felizpakas:742373250037710918> " +
                                        "Você removeu a permissão **" + permission + "** do jogador " + user.getName()
                        ).queue();
                        return;
                    }

                    hook.setEphemeral(true).sendMessage(":x: Você precisa usar **add** ou **remove** como parâmetro option.").queue();
                });
    }
}
