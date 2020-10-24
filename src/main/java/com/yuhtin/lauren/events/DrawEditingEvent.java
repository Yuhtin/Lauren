package com.yuhtin.lauren.events;

import com.yuhtin.lauren.Lauren;
import com.yuhtin.lauren.utils.helper.MathUtils;
import com.yuhtin.lauren.core.draw.controller.DrawController;
import com.yuhtin.lauren.models.enums.DrawEditingStatus;
import com.yuhtin.lauren.core.draw.controller.DrawEditting;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class DrawEditingEvent extends ListenerAdapter {

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        if (DrawController.editing == null
                || !DrawController.editing.userID.equals(event.getAuthor().getIdLong())) return;

        DrawEditting editting = DrawController.editing;
        String message = event.getMessage().getContentRaw();

        switch (editting.status) {
            case PRIZE: {
                if (message.length() <= 250) {
                    editting.prize = message;
                    editting.status = DrawEditingStatus.WINNERS;

                    EmbedBuilder embed = new EmbedBuilder()
                            .setTitle("\uD83D\uDCB3 Criando um sorteio")
                            .setDescription("\uD83D\uDCC4 Informações do sorteio:\n\n" +
                                    " ✏️ Item a ser sorteado: `" + message + "`\n" +
                                    " \uD83D\uDCCB Número de vencedores: `Digite no chat`")
                            .setTimestamp(Instant.now())
                            .setFooter("Editando as informações do sorteio", event.getJDA().getSelfUser().getAvatarUrl());

                    editting.message.editMessage(embed.build()).queue();
                    return;
                } else break;
            }
            case WINNERS: {
                int winners;
                try {
                    if (message.contains(" ")) break;
                    else winners = Integer.parseInt(message);

                    if (winners < 1 || winners > 50) break;
                } catch (Exception exception) {
                    break;
                }

                editting.winnersCount = winners;
                editting.status = DrawEditingStatus.TIME;

                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle("\uD83D\uDCB3 Criando um sorteio")
                        .setDescription("\uD83D\uDCC4 Informações do sorteio:\n\n" +
                                " ✏️ Item a ser sorteado: `" + editting.prize + "`\n" +
                                " \uD83D\uDCCB Número de vencedores: `" + MathUtils.plural(editting.winnersCount, "vencedor", "vencedores") + "`\n" +
                                " ⏳ Tempo de sorteio: `Digite no chat`")
                        .setTimestamp(Instant.now())
                        .setFooter("Editando as informações do sorteio", event.getJDA().getSelfUser().getAvatarUrl());

                editting.message.editMessage(embed.build()).queue();
                return;
            }
            case TIME: {
                int minutes = MathUtils.parseTime(message);

                if (minutes > 0) {
                    if (minutes > 1440) minutes = 1440;

                    editting.seconds = minutes * 60;
                    editting.status = DrawEditingStatus.CONFIRM;

                    EmbedBuilder embed = new EmbedBuilder()
                            .setTitle("\uD83D\uDCB3 Criando um sorteio")
                            .setDescription("\uD83D\uDCC4 Informações do sorteio:\n\n" +
                                    " ✏️ Item a ser sorteado: `" + editting.prize + "`\n" +
                                    " \uD83D\uDCCB Número de vencedores: `" + MathUtils.plural(editting.winnersCount, "vencedor", "vencedores") + "`\n" +
                                    " ⏳ Tempo de sorteio: `" + MathUtils.plural(minutes, "minuto", "minutos") + "`\n\n" +
                                    "♻️ Para confirmar o sorteio clique no emoji abaixo")
                            .setTimestamp(Instant.now())
                            .setFooter("Editando as informações do sorteio", event.getJDA().getSelfUser().getAvatarUrl());

                    editting.message.editMessage(embed.build()).queue();
                    editting.message.addReaction("\uD83D\uDE06").queue();
                    return;
                }
            }
        }
        event.getMessage().addReaction("❌").queue();
        event.getChannel().sendMessage("\uD83D\uDC94 Como assim??? Você quer quebrar meus sistemas? \uD83D\uDE2D")
                .queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
        event.getChannel().sendMessage("\uD83D\uDCCC " + getError(editting.status))
                .queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (event.getUser() == null || event.getUser().isBot() || DrawController.editing == null ||
                event.getMessageIdLong() != DrawController.editing.message.getIdLong() ||
                DrawController.editing.status != DrawEditingStatus.CONFIRM) return;

        if (DrawController.get() != null) {
            event.getChannel().sendMessage("\uD83D\uDE21 Alguém foi mais rápido e criou um sorteio enquanto você criava este \uD83D\uDE2D").queue();
            DrawController.editing = null;
            return;
        }

        DrawController.set(DrawController.editing.build());
        event.getChannel().sendMessage("<a:sino:731450603619745792> Você criou o sorteio com sucesso").queue();
        DrawController.editing = null;
    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
        if (event.getMember() == null || event.getMember().getUser().isBot()) return;

        if (DrawController.get() != null && DrawController.get().message.getIdLong() == event.getMessageIdLong() && !DrawController.get().finished)
            DrawController.get().users.remove(event.getUserIdLong());
    }

    private String getError(DrawEditingStatus status) {
        switch (status) {
            case PRIZE:
                return "Digite uma mensagem para prêmio menor";
            case WINNERS:
                return "Digite um valor de 1 a 50";
            default:
                return "Digite o tempo em minutos";
        }
    }
}
