package com.yuhtin.lauren.commands.utility;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.core.player.PlayerData;
import com.yuhtin.lauren.core.player.controller.PlayerDataController;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.time.Instant;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@CommandHandler(name = "apostar", type = CommandHandler.CommandType.UTILITY, description = "Apostar uma quantidade de dinheiro")
public class BetCommand extends Command {

    public BetCommand() {
        this.name = "apostar";
        this.aliases = new String[]{"bet", "aposta"};
    }

    @Override
    protected void execute(CommandEvent event) {
        event.getMessage().delete().queue();
        String[] arguments = event.getArgs().split(" ");

        if (arguments.length < 2) {
            event.getChannel().sendMessage(helpMessage(event.getMember().getColor()).build()).queue();
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
                color = "Amarelo";
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
                event.getChannel().sendMessage(helpMessage(event.getMember().getColor()).build()).queue();
                return;
            }
        }

        try {
            if (arguments[1].contains("NaN") || arguments[1].contains("-")) {
                event.getChannel().sendMessage(helpMessage(event.getMember().getColor()).build()).queue();
                return;
            }

            money = Integer.parseInt(arguments[1]);
        } catch (Exception exception) {
            event.getChannel().sendMessage(helpMessage(event.getMember().getColor()).build()).queue();
            return;
        }

        if (money < 20) {
            event.getChannel().sendMessage("<:chorano:726207542413230142> Você precisa apostar no mínimo `$20`").queue();
            return;
        }

        PlayerData data = PlayerDataController.get(event.getAuthor().getIdLong());
        if (data.money < money) {
            event.getChannel().sendMessage("<:chorano:726207542413230142> Você não tem dinheiro suficiente para realizar esta aposta.").queue();
            return;
        }

        if (new Random().nextInt(100) > chance) {
            data.removeMoney(money).save();
            event.getChannel().sendMessage("<:chorano:726207542413230142> Você perdeu `$" + money + "` tentando apostar na cor " + color)
                    .queue(message -> message.delete().queueAfter(5, TimeUnit.SECONDS));
            return;
        }

        int total = (int) (money * multiplier - (money));
        data.addMoney(total).save();
        event.getChannel().sendMessage("<a:palmas:726205273974374440> Você ganhou `+ $" + total + "` apostando na cor " + color)
                .queue(message -> message.delete().queueAfter(5, TimeUnit.SECONDS));
    }

    private EmbedBuilder helpMessage(Color color) {
        return new EmbedBuilder()
                .setTitle("<:chorano:726207542413230142> Apostas")
                .setDescription("Para realizar uma aposta utilize `$apostar <cor> <valor>`\n\n"
                        + "<a:feliz_2:726220815749611603> Cores:\n\n" +
                        "- <:nao_pertubar:703089222185386056> Vermelho (**1.5x**)\n" +
                        "- <:ausente:703089221774344224> Amarelo (**2x**)\n" +
                        "- <:online:703089222021808170> Verde (**5x**)")
                .setColor(color)
                .setTimestamp(Instant.now())
                .setFooter("Informações de apostas usado em");
    }

}
