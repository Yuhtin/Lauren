package events;

import logger.Logger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.time.Instant;
import java.util.Random;

public class MemberEvents extends ListenerAdapter {

    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor("O jogador " + event.getUser().getName() + " entrou no servidor");
        embedBuilder.setImage(event.getUser().getAvatarUrl());
        embedBuilder.setDescription("T\nA                  b");
        embedBuilder.setColor(Color.getHSBColor(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()));
        embedBuilder.setTimestamp(Instant.now());
        embedBuilder.setFooter("Usuário registrado ao servidor", event.getUser().getAvatarUrl());
        Logger.log("The player " + event.getUser().getName() + " joined on server").save();
        if (event.getGuild().getSystemChannel() == null) return;
        event.getGuild().getSystemChannel().sendMessage(embedBuilder.build()).queue();
    }
}
