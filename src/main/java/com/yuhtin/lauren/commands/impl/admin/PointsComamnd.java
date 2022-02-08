package com.yuhtin.lauren.commands.impl.admin;

import com.google.inject.Inject;
import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.commands.CommandData;
import com.yuhtin.lauren.utils.helper.UserUtil;
import lombok.val;
import lombok.var;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

import java.util.concurrent.TimeUnit;

@CommandData(
        name = "points",
        type = CommandData.CommandType.ADMIN,
        description = "Adicionar pontos de ranked para um jogador",
        args = {
                "<option>-Use add para adicionar ou remove para remover.",
                "<@player>-Jogador que deseja fazer a alteração.",
                "<!quantity>-Quantidade de pontos que deseja adicionar ou remover."
        }
)
public class PointsComamnd implements Command {

    @Inject private PlayerController playerController;

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) throws Exception {
        if (event.getMember() == null
                || !UserUtil.hasPermission(event.getMember(), hook, Permission.ADMINISTRATOR))
            return;

        val option = event.getOption("option").getAsString();
        val player = event.getOption("player").getAsUser();
        var quantity = (int) event.getOption("quantity").getAsDouble();
        if (option.equalsIgnoreCase("remove")) quantity *= -1;

        val data = playerController.get(player.getIdLong());
        data.setRankedPoints(data.getRankedPoints() + quantity);
        data.updateRank();

        if (quantity <= 0) quantity *= -1;
        hook.sendMessage("<:felizpakas:742373250037710918> " +
                        "Você " + (option.equalsIgnoreCase("remove") ? "removeu" : "adicionou") +
                        " **" + quantity + "** pontos ao jogador " + player.getAsTag())
                .queue();
    }
}
