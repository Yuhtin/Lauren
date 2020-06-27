package com.yuhtin.lauren.core.draw;

import com.yuhtin.lauren.core.draw.controller.DrawController;
import com.yuhtin.lauren.utils.helper.MathUtils;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
public class Draw {

    private final ScheduledExecutorService schedule = Executors.newScheduledThreadPool(1);

    public final String prize;
    public final int winners;
    public final Instant end;
    public final long user;
    public final TextChannel channel;
    public final List<Long> users = new ArrayList<>();
    public Message message;
    public boolean finished;

    public void send() {
        channel.sendMessage(render()).queue(m -> {
            message = m;
            m.addReaction("⭐").queue();
        });
        schedule.scheduleWithFixedDelay(this::update, 1, 1, TimeUnit.MINUTES);
    }

    public Message render() {
        Instant now = Instant.now();
        String time = MathUtils.format(end.toEpochMilli() - System.currentTimeMillis());
        if (time.equalsIgnoreCase("")) {
            finish();
            return null;
        }

        MessageBuilder mb = new MessageBuilder();
        boolean close = now.plusSeconds(9).isAfter(end);
        mb.append(close ? "\uD83C\uDFA8 **SORTEIO FINALIZADO** \uD83C\uDFA8" : "\uD83C\uDFB2 **SORTEIO** \uD83C\uDFB2");

        EmbedBuilder eb = new EmbedBuilder();

        eb.setColor(Color.DARK_GRAY);
        eb.setFooter((winners == 1 ? "" : winners + " ganhadores | ") + "Encerra em", null);
        eb.setTimestamp(end);
        eb.setDescription("Clique no ⭐ para entrar!"
                + "\nTempo restante: " + time
                + "\nSorteador: <@" + user + ">");

        if (prize != null) eb.setAuthor(prize, null, null);
        if (close) eb.setTitle("Última chance para entrar", null);

        mb.setEmbed(eb.build());
        mb.append(" @everyone");

        return mb.build();
    }

    public void update() {
        Message render = render();
        if (render == null) return;

        message.editMessage(render).queue(m -> {
        }, t -> {
            message.delete().queue();
            DrawController.delete();
        });
    }

    public void finish() {
        finished = true;
        MessageBuilder mb = new MessageBuilder();
        mb.append("\uD83C\uDFA8 **SORTEIO FINALIZADO** \uD83C\uDFA8");

        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.DARK_GRAY);
        eb.setFooter((winners == 1 ? "" : winners + " Winners | ") + "Finalizado no dia", null);
        eb.setTimestamp(end);
        if (prize != null) eb.setAuthor(prize, null, null);

        try {
            rollWinners(mb, eb);

        } catch (Exception e) {
            mb.setEmbed(eb.setDescription("Não foi possível determinar um vencedor\nSorteador: <@" + user + ">").build());

            message.editMessage(mb.build()).queue();
            channel.sendMessage("\uD83E\uDD26\uD83C\uDFFD Oh Deus, desde quando tem pessoas que não querem ganhar sorteios DE GRAÇA, em que mundo estou \uD83D\uDE2D").queue();
        }

        schedule.shutdown();
    }

    private void rollWinners(MessageBuilder mb, EmbedBuilder eb) {
        List<Long> wins = getWinners(users);
        StringBuilder toSend;
        if (wins.isEmpty()) {
            eb.setDescription("Ninguém venceu o sorteio");
            toSend = new StringBuilder("\uD83E\uDD26\uD83C\uDFFD Oh Deus, desde quando tem pessoas que não querem ganhar sorteios DE GRAÇA, em que mundo estou \uD83D\uDE2D");

        } else if (wins.size() == 1) {
            eb.setDescription("Vencedor: <@" + wins.get(0) + ">");
            toSend = new StringBuilder("Parabéns <@" + wins.get(0) + ">, você ganhou o sorteio de `" + prize + "`");

        } else {
            eb.setDescription("Vencedores:");
            wins.forEach(w -> eb.appendDescription("\n").appendDescription("<@" + w + ">"));

            toSend = new StringBuilder("Parabéns <@" + wins.get(0) + ">");
            for (int i = 1; i < wins.size(); i++) toSend.append(", <@").append(wins.get(i)).append(">");

            toSend.append(", vocês ganharam o sorteio de `").append(prize).append("`");
        }
        mb.setEmbed(eb.appendDescription("\nSorteador: <@" + user + ">").build());

        message.editMessage(mb.build()).queue();
        channel.sendMessage(toSend).queue();
    }

    public List<Long> getWinners(List<Long> list) {
        List<Long> winList = new LinkedList<>();
        List<Long> cloneList = new LinkedList<>(list);

        for (int i = 0; i < winners; i++) {
            winList.add(cloneList.remove((int) (Math.random() * cloneList.size())));
        }

        return winList;
    }
}