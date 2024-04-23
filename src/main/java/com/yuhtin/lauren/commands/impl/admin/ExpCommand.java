package com.yuhtin.lauren.commands.impl.admin;

import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import com.yuhtin.lauren.commands.CommandType;
import com.yuhtin.lauren.module.Module;
import com.yuhtin.lauren.module.impl.player.module.PlayerModule;
import lombok.val;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

@CommandInfo(
        name = "exp",
        type = CommandType.CONFIG,
        description = "Alterar o exp de um jogador",
        args = {
                "<option>-Use add, remove ou set.",
                "<@player>-Jogador que deseja fazer a alteração.",
                "<!quantity>-Quantidade de exp que deseja adicionar ou remover."
        },
        permissions = {Permission.ADMINISTRATOR}
)
public class ExpCommand implements Command {

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) throws Exception {
        if (event.getMember() == null) return;

        String option = event.getOption("option").getAsString();
        User user = event.getOption("player").getAsUser();

        Module.instance(PlayerModule.class)
                .retrieve(user.getIdLong())
                .thenAccept(player -> {
                    int quantity = event.getOption("quantity").getAsInt();
                    if (option.equalsIgnoreCase("remove")) quantity *= -1;

                    if (!option.equalsIgnoreCase("set")) quantity += player.getExperience();

                    player.setExperience(quantity);
                    player.updateRank();

                    if (quantity <= 0) quantity *= -1;
                    val optionUsed = option.equalsIgnoreCase("set") ? "setou" : option.equalsIgnoreCase("remove") ? "removeu" : "adicionou";

                    hook.sendMessage("<:felizpakas:742373250037710918> Você " + optionUsed + " **" + quantity + "** exp ao jogador " + user.getName()).queue();
                });
    }

}
