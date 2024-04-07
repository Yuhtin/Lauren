package com.yuhtin.lauren.commands.impl.utility;

import com.google.inject.Inject;
import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

import java.awt.*;
import java.time.Instant;
import java.util.Random;

@CommandInfo(
        name = "apostar",
        type = CommandType.UTILITY,
        description = "Multiplique seus shards apostando",
        args = {
                "[cor]-Cor que deseja apostar",
                "[!quantia]-Quantia de shards que deseja apostar"
        }
)
public class BetCommand implements Command {

    private static final Random RANDOM = new Random();

    @Inject
    private PlayerController playerController;

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) throws Exception {
        val colorOption = event.getOption("cor");
        val quantityOption = event.getOption("quantia");

        val member = event.getMember();
        if (colorOption == null || quantityOption == null) {
            hook.sendMessageEmbeds(helpMessage(member.getColor(), event.getGuild().getIconUrl()).build()).queue();
            return;
        }

        String color;
        double multiplier;
        int chance;
        int money;

        switch (colorOption.getAsString()) {
            case "vermelho":
                color = "Vermelho";
                multiplier = 1.5;
                chance = 50;
                break;

            case "amarelo":
                color = "Amarela";
                multiplier = 2;
                chance = 19;
                break;

            case "verde":
                color = "Verde";
                multiplier = 5;
                chance = 5;
                break;

            default:
                hook.sendMessageEmbeds(helpMessage(member.getColor(), event.getGuild().getIconUrl()).build()).queue();
                return;
        }

        try {
            money = (int) quantityOption.getAsDouble();
            if (NumberUtils.isInvalid(money)) {
                hook.sendMessageEmbeds(helpMessage(member.getColor(), event.getGuild().getIconUrl()).build()).queue();
                return;
            }
        } catch (Exception exception) {
            hook.sendMessageEmbeds(helpMessage(member.getColor(), event.getGuild().getIconUrl()).build()).queue();
            return;
        }

        val user = event.getUser();
        if (money < 20) {
            hook.sendMessage("<:chorano:726207542413230142> <@" + user.getId() + ">, você precisa apostar no mínimo `20 shards`").queue();
            return;
        }

        val data = playerController.get(user.getIdLong());
        if (data.getMoney() < money) {
            hook.sendMessage("<:chorano:726207542413230142> <@" + user.getId() + ">, você não tem dinheiro suficiente para realizar esta aposta.").queue();
            return;
        }

        if (RANDOM.nextInt(100) > chance) {
            data.removeMoney(money);
            hook.sendMessage("<:chorano:726207542413230142> <@" + user.getId() + ">, você perdeu `" + money + " shards` tentando apostar na cor " + color).queue();
            return;
        }

        int total = (int) (money * multiplier - (money));
        data.addMoney(total);
        hook.sendMessage("<:felizpakas:742373250037710918> <@" + user.getId() + ">, você ganhou `+ " + total + " shards` apostando na cor " + color).queue();
    }

    private EmbedBuilder helpMessage(Color color, String iconUrl) {
        return new EmbedBuilder()
                .setTitle("<:chorano:726207542413230142> Apostas")
                .setDescription("Para realizar uma aposta utilize `/apostar`\n\n"
                        + "<a:feliz_2:726220815749611603> Cores:\n\n" +
                        "- <:nao_pertubar:703089222185386056> Vermelho (**1.5x**)\n" +
                        "- <:ausente:703089221774344224> Amarelo (**2x**)\n" +
                        "- <:online:703089222021808170> Verde (**5x**)")
                .setColor(color)
                .setTimestamp(Instant.now())
                .setFooter("Sistema de apostas", iconUrl);
    }

}
