package com.yuhtin.lauren.commands.utility;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import net.dv8tion.jda.api.EmbedBuilder;

import javax.inject.Inject;
import java.awt.*;
import java.time.Instant;
import java.util.Random;

@CommandHandler(
        name = "apostar",
        type = CommandHandler.CommandType.UTILITY,
        description = "Apostar uma quantidade de dinheiro",
        alias = {"bet", "aposta"})
public class BetCommand extends Command {

    @Inject private PlayerController playerController;

    @Override
    protected void execute(CommandEvent event) {
        String[] arguments = event.getArgs().split(" ");

        if (arguments.length < 2) {
            event.getChannel().sendMessage(helpMessage(event.getMember().getColor(), event.getGuild().getIconUrl()).build()).queue();
            return;
        }

        String color;
        double multiplier;
        int chance, money;

        switch (arguments[0].toLowerCase()) {
            case "vermelho": {
                color = "Vermelho";
                multiplier = 1.5;
                chance = 50;
                break;
            }

            case "amarelo": {
                color = "Amarela";
                multiplier = 2;
                chance = 19;
                break;
            }

            case "verde": {
                color = "Verde";
                multiplier = 5;
                chance = 5;
                break;
            }

            default: {
                event.getChannel().sendMessage(helpMessage(event.getMember().getColor(), event.getGuild().getIconUrl()).build()).queue();
                return;
            }
        }

        try {
            if (arguments[1].contains("NaN") || arguments[1].contains("-")) {
                event.getChannel().sendMessage(helpMessage(event.getMember().getColor(), event.getGuild().getIconUrl()).build()).queue();
                return;
            }

            money = Integer.parseInt(arguments[1]);
        } catch (Exception exception) {
            event.getChannel().sendMessage(helpMessage(event.getMember().getColor(), event.getGuild().getIconUrl()).build()).queue();
            return;
        }

        if (money < 20) {
            event.getChannel().sendMessage("<:chorano:726207542413230142> <@" + event.getAuthor().getId() + ">, você precisa apostar no mínimo `$20`").queue();
            return;
        }

        Player data = this.playerController.get(event.getAuthor().getIdLong());
        if (data.getMoney() < money) {
            event.getChannel().sendMessage("<:chorano:726207542413230142> <@" + event.getAuthor().getId() + ">, você não tem dinheiro suficiente para realizar esta aposta.").queue();
            return;
        }

        if (new Random().nextInt(100) > chance) {
            data.removeMoney(money);
            event.getChannel().sendMessage("<:chorano:726207542413230142> <@" + event.getAuthor().getId() + ">, você perdeu `$" + money + "` tentando apostar na cor " + color).queue();
            return;
        }

        int total = (int) (money * multiplier - (money));
        data.addMoney(total);
        event.getChannel().sendMessage("<:felizpakas:742373250037710918> <@" + event.getAuthor().getId() + ">, você ganhou `+ $" + total + "` apostando na cor " + color).queue();
    }

    private EmbedBuilder helpMessage(Color color, String iconUrl) {
        return new EmbedBuilder()
                .setTitle("<:chorano:726207542413230142> Apostas")
                .setDescription("Para realizar uma aposta utilize `$apostar <cor> <valor>`\n\n"
                        + "<a:feliz_2:726220815749611603> Cores:\n\n" +
                        "- <:nao_pertubar:703089222185386056> Vermelho (**1.5x**)\n" +
                        "- <:ausente:703089221774344224> Amarelo (**2x**)\n" +
                        "- <:online:703089222021808170> Verde (**5x**)")
                .setColor(color)
                .setTimestamp(Instant.now())
                .setFooter("Sistema de apostas", iconUrl);
    }

}
