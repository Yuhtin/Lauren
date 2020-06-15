package events.messages;

import application.Lauren;
import draw.controller.DrawController;
import draw.controller.DrawEditingStatus;
import draw.controller.DrawEditting;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import utils.helper.MathUtils;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class DrawEditingEvent extends ListenerAdapter {

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        if (DrawController.editing == null || !DrawController.editing.userID.equals(event.getAuthor().getIdLong())) {
            event.getChannel().sendMessage("\uD83D\uDC94 Você só pode brincar comigo no servidor `" + Lauren.guild.getName() + "`").queue();
            return;
        }

        DrawEditting editting = DrawController.editing;
        String message = event.getMessage().getContentRaw();
        boolean error = false;

        switch (editting.status) {
            case PRIZE: {
                if (message.length() <= 250) {
                    editting.prize = message;
                    editting.status = DrawEditingStatus.WINNERS;

                    EmbedBuilder embed = new EmbedBuilder()
                            .setAuthor("\uD83D\uDCB3 Criando um sorteio")
                            .setDescription("\uD83D\uDCC4 Informações do sorteio:\n\n" +
                                    " ✏️ Item a ser sorteado: `" + message + "`\n" +
                                    " \uD83D\uDCCB Número de vencedores: `Digite no chat`")
                            .setTimestamp(Instant.now())
                            .setFooter("Editando as informações do sorteio", event.getJDA().getSelfUser().getAvatarUrl());

                    editting.message.editMessage(embed.build()).queue();
                    return;
                }
            }
            case WINNERS: {
                int winners = 0;
                try {
                    if (message.contains(" ")) error = true;
                    else winners = Integer.parseInt(message);

                    if (winners < 1 || winners > 50) error = true;
                } catch (Exception exception) {
                    error = true;
                }

                if (!error) {
                    editting.winnersCount = winners;
                    editting.status = DrawEditingStatus.TIME;

                    EmbedBuilder embed = new EmbedBuilder()
                            .setAuthor("\uD83D\uDCB3 Criando um sorteio")
                            .setDescription("\uD83D\uDCC4 Informações do sorteio:\n\n" +
                                    " ✏️ Item a ser sorteado: `" + editting.prize + "`\n" +
                                    " \uD83D\uDCCB Número de vencedores: `" + winners + " " + MathUtils.plural(editting.winnersCount, "vencedor", "vencedores") + "`\n" +
                                    " ⏳ Tempo de sorteio: `Digite no chat`")
                            .setTimestamp(Instant.now())
                            .setFooter("Editando as informações do sorteio", event.getJDA().getSelfUser().getAvatarUrl());

                    editting.message.editMessage(embed.build()).queue();
                    return;
                }
            }
            case TIME: {
                int minutes = MathUtils.parseTime(message);

                if (minutes > 0) {
                    editting.seconds = minutes * 60;
                    editting.status = DrawEditingStatus.CONFIRM;

                    EmbedBuilder embed = new EmbedBuilder()
                            .setAuthor("\uD83D\uDCB3 Criando um sorteio")
                            .setDescription("\uD83D\uDCC4 Informações do sorteio:\n\n" +
                                    " ✏️ Item a ser sorteado: `" + editting.prize + "`\n" +
                                    " \uD83D\uDCCB Número de vencedores: `" + editting.winnersCount + " " + MathUtils.plural(editting.winnersCount, "vencedor", "vencedores") + "`\n" +
                                    " ⏳ Tempo de sorteio: `" + minutes + " " + MathUtils.plural(minutes, "minuto", "minutos") + "`\n\n" +
                                    "♻️ Para confirmar o sorteio clique no emoji abaixo")
                            .setTimestamp(Instant.now())
                            .setFooter("Editando as informações do sorteio", event.getJDA().getSelfUser().getAvatarUrl());

                    editting.message.editMessage(embed.build()).queue();
                    editting.message.addReaction(":sim:704295025374265387").queue();
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
        event.getChannel().sendMessage("\uD83D\uDD14 Você criou o sorteio com sucesso").queue();
        DrawController.editing = null;
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
