package com.yuhtin.lauren.commands.impl.admin;

import com.google.inject.Inject;
import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.utils.UserUtil;
import lombok.val;
import lombok.var;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

@CommandInfo(
        name = "exp",
        type = CommandInfo.CommandType.CONFIG,
        description = "Alterar o exp de um jogador",
        args = {
                "<option>-Use add, remove ou set.",
                "<@player>-Jogador que deseja fazer a alteração.",
                "<!quantity>-Quantidade de exp que deseja adicionar ou remover."
        }
)
public class ExpCommand implements Command {

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
        if (!option.equalsIgnoreCase("set")) quantity += data.getRankedPoints();

        data.setRankedPoints(quantity);
        data.updateRank();

        if (quantity <= 0) quantity *= -1;
        val optionUsed = option.equalsIgnoreCase("set") ? "setou" : option.equalsIgnoreCase("remove") ? "removeu" : "adicionou";

        hook.sendMessage("<:felizpakas:742373250037710918> " +
                        "Você " + optionUsed + " **" + quantity + "** exp ao jogador " + player.getAsTag())
                .queue();
    }

}
