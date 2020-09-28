package com.yuhtin.lauren.commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.yuhtin.lauren.core.music.TrackManager;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import com.yuhtin.lauren.utils.helper.TrackUtils;
import org.jmusixmatch.MusixMatch;
import org.jmusixmatch.entity.lyrics.Lyrics;
import org.jmusixmatch.entity.track.TrackData;

@CommandHandler(
        name = "letra",
        type = CommandHandler.CommandType.MUSIC,
        description = "Ver a letra da música atual",
        alias = {"letter", "lyrics"}
)
public class LyricsCommand extends Command {

    @Override
    protected void execute(CommandEvent event) {
        StringBuilder builder = new StringBuilder();
        String[] arguments = event.getArgs().split(" ");
        String name, author;

        if (arguments.length == 1) {
            if (TrackUtils.get().isIdle(event.getTextChannel())) return;

            AudioTrackInfo audio = TrackManager.get().getTrackInfo().getTrack().getInfo();
            name = audio.title;
            author = audio.author;

        } else if (arguments.length >= 2) {
            for (int i = 1; i < arguments.length; i++) builder.append(arguments[i]).append(" ");
            name = builder.toString();
            author = arguments[0];
        }else {
            event.getChannel().sendMessage("<:chorano:726207542413230142> Insira o nome da musica e do autor: $letra (autor) (musica)").queue();
            return;
        }

        MusixMatch musixMatch = new MusixMatch("87194844049666343ccc929553a3dcb6");
        try {
            TrackData track = musixMatch.getMatchingTrack(name, author).getTrack();
            if (track.getHasLyrics() == 1) {
                Lyrics lyrics = musixMatch.getLyrics(track.getTrackId());
                event.getChannel().sendMessage(lyrics.getLyricsBody()).queue();
            } else event.getChannel().sendMessage("<:chorano:726207542413230142> Não encontrei esta música em minha biblioteca, desculpe-me").queue();
        } catch (Exception exception) {
            event.getChannel().sendMessage("<:chorano:726207542413230142> Não encontrei esta música em minha biblioteca, desculpe-me").queue();
        }
    }
}
