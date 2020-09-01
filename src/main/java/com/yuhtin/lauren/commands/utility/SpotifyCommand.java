package com.yuhtin.lauren.commands.utility;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import com.yuhtin.lauren.utils.helper.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.RichPresence;

import java.awt.*;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

@CommandHandler(
        name = "spotify",
        type = CommandHandler.CommandType.UTILITY,
        description = "Veja as informações do spotify de um usuário",
        alias = {}
)
public class SpotifyCommand extends Command {

    public SpotifyCommand() {
        this.name = "spotify";
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getMessage().getMentionedMembers().size() < 1) {
            event.getChannel().sendMessage("Ops, você precisa mencionar um jogador para receber os pontos")
                    .queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
            return;
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTimestamp(Instant.now());
        embed.setFooter("Comando usado por " + Utilities.INSTANCE.getFullName(event.getMember().getUser()), event.getMember().getUser().getAvatarUrl());

        Member member = event.getMessage().getMentionedMembers().get(0);
        if (member.getActivities().size() < 1
                || member.getActivities().get(0).getType() != Activity.ActivityType.LISTENING
                || !member.getActivities().get(0).getName().equalsIgnoreCase("Spotify")) {
            embed.setColor(member.getColor());

            if (member.getVoiceState() != null
                    && member.getVoiceState().getChannel() != null
                    && member.getVoiceState()
                    .getChannel()
                    .getName()
                    .equalsIgnoreCase("\uD83C\uDFB6┇Batidões")) {
                embed.setAuthor("👻 Usuário conectado na rádio");
                embed.setDescription("Este usuário está antenado em minha rádio 😎\nAproveita e da uma passada lá 🤩");
            } else {
                embed.setAuthor("❌ Erro");
                embed.addField("Este usuário não está ouvindo nada", "Chame ele pra escutar algo :D", true);
            }
        } else {
            Activity activity = member.getActivities().get(0);
            RichPresence richPresence = activity.asRichPresence();

            embed.setAuthor("\uD83C\uDFA7 Informações do Spotify de " + member.getUser().getName());
            embed.setColor(Color.getColor("00D000"));
            embed.setThumbnail(richPresence.getLargeImage().getUrl());

            embed.setDescription("\uD83C\uDFB6  **Nome da música:** " + richPresence.getDetails() +
                    "\n\uD83C\uDF00 **Autor(es):** " + richPresence.getState().replace(";", ",") +
                    "\n" +
                    "\n\uD83D\uDCBE **Escute também:** [" + richPresence.getDetails() + "](https://open.spotify.com/track/" + richPresence.getSessionId() + ")");
        }

        event.getChannel().sendMessage(embed.build()).queue();
    }


}
