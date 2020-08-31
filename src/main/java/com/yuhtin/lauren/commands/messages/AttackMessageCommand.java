package com.yuhtin.lauren.commands.messages;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import java.awt.*;
import java.time.Instant;

@CommandHandler(name = "attacktest",
        type = CommandHandler.CommandType.CUSTOM_MESSAGES,
        description = "Mensagem customizada que será enviado ao hacker",
        alias = {"attack", "atacar", "hack"})
public class AttackMessageCommand extends Command {

    public AttackMessageCommand() {
        this.name = "attacktest";
        this.aliases = new String[]{"attack", "atacar", "hack"};
    }

    @Override
    protected void execute(CommandEvent event) {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("RedeLegit tem o dono que merece?");
        embed.setFooter("Exposed Herobosta dono da RedeLegit", event.getGuild().getIconUrl());

        embed.setDescription(
                "Você sabia que a **maioria** dos sistemas da RedeLegit\n" +
                        "são da internet e por isso possuem vários **erros**?\n\n" +
                        "Você sabia que o dono do RedeLegit, Heroboss, usa **hack** e continua usando? " +
                        "Não assumiu em público ter **abusado** no antigo factions, além de dar a desculpa de ter xitado " +
                        "no **próprio servidor** falando que haviam xitados contra ele, " +
                        "sendo que ele é o próprio **dono** e pode banir-los.\n\n" +
                        "Como a desculpa não ia pegar bem, falou que era **bug de chunk ou visual**, " +
                        "essa ideia veio do próprio DCManager (**SrWhale**), " +
                        "e até hoje não quis falar a **verdade**\n\n" +
                        "Bem, tem **muito mais**, se quiser saber, veja o **vídeo** abaixo\n\n" +
                        "**OBS**: Não aguenta a verdade Hero? Processa ai 😥");

        embed.setImage("https://image.prntscr.com/image/xLXkXtzVRZa0jmU0zlC9Kg.png");
        embed.setThumbnail("https://image.prntscr.com/image/uGoqWKiST6S_wFtVOgTCOw.png");
        embed.setColor(Color.CYAN);
        embed.setTimestamp(Instant.now());

        event.getChannel().sendMessage(embed.build()).queue();
        event.getChannel().sendMessage("link1\nlink2").queue();
    }
}
