package com.yuhtin.lauren.commands.suport.draw;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.core.draw.controller.DrawController;
import com.yuhtin.lauren.core.draw.controller.DrawEditting;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import com.yuhtin.lauren.models.enums.LogType;
import com.yuhtin.lauren.utils.helper.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;

import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@CommandHandler(
        name = "sorteio",
        type = CommandHandler.CommandType.SUPORT,
        description = "Iniciar um sorteio sobre algum conteúdo",
        alias = {"sortear", "draw"})
public class DrawCommand extends Command {

    public DrawCommand() {
        this.name = "sorteio";
        this.aliases = new String[]{"sortear", "draw"};
    }


    @Override
    protected void execute(CommandEvent event) {
        if (!Utilities.isPermission(event.getMember(), event.getChannel(), Permission.ADMINISTRATOR, true)) return;

        if (DrawController.get() != null || DrawController.editing != null) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("\uD83D\uDC94 Erro ao criar um sorteio")
                    .setDescription("\uD83D\uDCA1 Já existe um sorteio ativo ou alguém está criando um");
            event.getChannel().sendMessage(embed.build()).queue();
            return;
        }

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("\uD83D\uDCB3 Criando um sorteio")
                .setDescription("\uD83D\uDCE5 Você recebeu uma mensagem no privado para criar um sorteio, digite as informações do mesmo em seu privado \n" +
                        "⚠️ Você tem até `2 minutos` para criar o sorteio\n\n" +
                        "\uD83D\uDCCC Caso não tenha recebido a mensagem, ative suas mensagens diretas neste servidor.");
        event.getChannel().sendMessage(embed.build()).queue();

        EmbedBuilder privateEmbed = new EmbedBuilder()
                .setTitle("\uD83D\uDCB3 Criando um sorteio")
                .setDescription("\uD83D\uDCC4 Informações do sorteio:\n\n" +
                        " ✏️ Item a ser sorteado: `Digite no chat`")
                .setTimestamp(Instant.now())
                .setFooter("Editando as informações do sorteio", event.getJDA().getSelfUser().getAvatarUrl());

        try {
            event.getMember().getUser().openPrivateChannel().queue(channel -> channel.sendMessage(privateEmbed.build()).queue(m -> {
                DrawController.editing = new DrawEditting(m, event.getMember().getIdLong(), event.getMember().getUser().openPrivateChannel());
            }));

            ScheduledExecutorService schedule = Executors.newScheduledThreadPool(1);
            schedule.schedule(() -> {
                if (DrawController.get() == null) {

                    try {
                        event.getMember().getUser().openPrivateChannel().queue(channel -> channel.sendMessage("\uD83D\uDC94 Você demorou muito para criar o sorteio").queue());
                        DrawController.editing = null;

                    } catch (Exception exception) {
                        Logger.log("Can't send a private message for user " + Utilities.getFullName(event.getMember().getUser()));
                    }
                }
            }, 2, TimeUnit.MINUTES);

        } catch (Exception exception) {
            Logger.log("Can't send a private message for user " + Utilities.getFullName(event.getMember().getUser()));
        }
    }
}
