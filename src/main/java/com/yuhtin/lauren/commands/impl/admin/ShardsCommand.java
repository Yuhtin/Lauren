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
        name = "shard",
        type = CommandType.ADMIN,
        description = "Alterar shards de um jogador",
        args = {
                "<option>-Use add, remove ou set.",
                "<@player>-Jogador que deseja fazer a alteração.",
                "<!quantity>-Quantidade de shards que deseja adicionar ou remover."
        },
        permissions = {Permission.ADMINISTRATOR}
)
public class ShardsCommand implements Command {

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) throws Exception {
        if (event.getMember() == null) return;

        val option = event.getOption("option").getAsString();
        val user = event.getOption("player").getAsUser();


        PlayerModule playerModule = Module.instance(PlayerModule.class);
        if (playerModule == null) {
            hook.sendMessage("Módulo de jogador não carregado!").queue();
            return;
        }

        playerModule.retrieve(user.getIdLong())
                .thenAccept(player -> {
                    var quantity = (int) event.getOption("quantity").getAsDouble();
                    if (option.equalsIgnoreCase("remove")) quantity *= -1;

                    if (option.equalsIgnoreCase("set")) {
                        player.setMoney(quantity);
                    } else {
                        player.addMoney(quantity);
                    }

                    if (quantity <= 0) quantity *= -1;
                    val optionUsed = option.equalsIgnoreCase("set") ? "setou" : option.equalsIgnoreCase("remove") ? "removeu" : "adicionou";

                    hook.sendMessage("<:felizpakas:742373250037710918> " +
                                    "Você " + optionUsed + " **" + quantity + "** shards ao jogador " + user.getName())
                            .queue();
                });


    }
}
