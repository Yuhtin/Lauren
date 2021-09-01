package com.yuhtin.lauren.commands.impl.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.yuhtin.lauren.core.music.TrackManager;
import com.yuhtin.lauren.commands.CommandHandler;
import com.yuhtin.lauren.utils.helper.TrackUtils;
import net.dv8tion.jda.api.EmbedBuilder;

@CommandHandler(
        name = "musica",
        type = CommandHandler.CommandType.MUSIC,
        description = "Ver as informações da música atual",
        alias = {"music", "tocando"}
)
public class MusicCommand extends Command {

    @Override
    protected void execute(CommandEvent event) {
        if (TrackUtils.get().isIdle(event.getTextChannel())) return;

        TrackManager trackManager = TrackManager.of(event.getGuild());

        AudioTrack track = trackManager.getPlayer().getPlayingTrack();
        String isRepeating = trackManager.getTrackInfo().isRepeat() ? "`Ativa`" : "`Desativada`";

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("\ud83d\udcbf Informações da música atual");
        embed.setDescription("\ud83d\udcc0 Nome: `" + track.getInfo().title + "`\n" +
                "\uD83D\uDCB0 Autor: `" + track.getInfo().author + "`\n" +
                "\uD83D\uDCE2 Tipo de vídeo: `" + (track.getInfo().isStream ? "Stream" : track.getInfo().title.contains("Podcast") ? "Podcast" : "Música") + "`\n" +
                "<a:infinito:703187274912759899> Repetição: " + isRepeating + "\n" +
                "\uD83E\uDDEC Membro que adicionou: <@" + trackManager.getTrackInfo().getAuthor().getIdLong() + ">\n" +
                "\uD83E\uDDEA Timeline: " + "⏸ ⏭ \uD83D\uDD0A " + TrackUtils.get().getProgressBar(track) + "\n" +
                "\n" +
                "\uD83D\uDCCC Link: [Clique aqui](" + track.getInfo().uri + ")");

        event.getChannel().sendMessage(embed.build()).queue();
    }
}
