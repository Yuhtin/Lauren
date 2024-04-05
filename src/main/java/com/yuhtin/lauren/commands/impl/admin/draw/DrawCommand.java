package com.yuhtin.lauren.commands.impl.admin.draw;

import com.google.inject.Inject;
import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import com.yuhtin.lauren.core.draw.controller.DrawController;
import com.yuhtin.lauren.core.draw.controller.DrawEditting;
import com.yuhtin.lauren.core.logger.Logger;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@CommandInfo(
        name = "sortear",
        type = CommandInfo.CommandType.ADMIN,
        description = "Iniciar um sorteio sobre algum conteúdo"
)
public class DrawCommand implements Command {

    @Inject private Logger logger;

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) throws Exception {
        if (event.getMember() == null
                || !UserUtil.hasPermission(event.getMember(), hook, Permission.ADMINISTRATOR)) return;

        if (DrawController.get() != null || DrawController.editing != null) {
            val embed = new EmbedBuilder()
                    .setTitle("\uD83D\uDC94 Erro ao criar um sorteio")
                    .setDescription("\uD83D\uDCA1 Já existe um sorteio ativo ou alguém está criando um");
            hook.sendMessageEmbeds(embed.build()).queue();
            return;
        }

        val embed = new EmbedBuilder()
                .setTitle("\uD83D\uDCB3 Criando um sorteio")
                .setDescription("\uD83D\uDCE5 Você recebeu uma mensagem no privado para criar um sorteio, digite as informações do mesmo em seu privado \n" +
                        "⚠️ Você tem até `2 minutos` para criar o sorteio\n\n" +
                        "\uD83D\uDCCC Caso não tenha recebido a mensagem, ative suas mensagens diretas neste servidor.");

        hook.sendMessageEmbeds(embed.build()).queue();

        val privateEmbed = new EmbedBuilder()
                .setTitle("\uD83D\uDCB3 Criando um sorteio")
                .setDescription("\uD83D\uDCC4 Informações do sorteio:\n\n" +
                        " ✏️ Item a ser sorteado: `Digite no chat`")
                .setTimestamp(Instant.now())
                .setFooter("Editando as informações do sorteio", event.getJDA().getSelfUser().getAvatarUrl());

        try {
            event.getMember().getUser().openPrivateChannel().queue(channel -> {
                channel.sendMessageEmbeds(privateEmbed.build()).queue(message -> {
                    DrawController.editing = new DrawEditting(
                            message,
                            event.getMember().getIdLong(),
                            event.getMember().getUser().openPrivateChannel()
                    );
                });
            });

            ScheduledExecutorService schedule = Executors.newScheduledThreadPool(1);
            schedule.schedule(() -> {
                if (DrawController.get() == null) {
                    try {
                        event.getMember().getUser().openPrivateChannel().queue(channel -> {
                            channel.sendMessage("\uD83D\uDC94 Você demorou muito para criar o sorteio").queue();
                        });

                        DrawController.editing = null;
                    } catch (Exception exception) {
                        this.logger.warning("Can't send a private message for user " + event.getUser().getName());
                    }
                }
            }, 2, TimeUnit.MINUTES);

        } catch (Exception exception) {
            this.logger.warning("Can't send a private message for user " + event.getUser().getName());
        }
    }
}
