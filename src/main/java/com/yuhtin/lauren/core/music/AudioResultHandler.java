package com.yuhtin.lauren.core.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.statistics.controller.StatsController;
import com.yuhtin.lauren.utils.helper.TaskHelper;
import com.yuhtin.lauren.utils.helper.Utilities;
import lombok.Builder;
import lombok.Data;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.concurrent.TimeUnit;

@Builder
@Data
public final class AudioResultHandler implements AudioLoadResultHandler {

    private final String trackUrl;
    private final Member member;
    private final TextChannel channel;
    private final TrackManager.SearchType searchType;

    @Override
    public void trackLoaded(AudioTrack track) {

        if (!permitedTrack(track)) {

            if (searchType == TrackManager.SearchType.SIMPLE_SEARCH)
                channel.sendMessage("<:rindo_de_voce:751941649655136588>" +
                        " Sua música foi bloqueada por ser muito grande ou ser 'som de' algo")
                        .queue();
            return;

        }


        if (TrackManager.get().player.isPaused()) TrackManager.get().player.setPaused(false);

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("💿 " + Utilities.INSTANCE.getFullName(member.getUser()) + " adicionou 1 música a fila")
                .setDescription(
                        "\ud83d\udcc0 Nome: `" + track.getInfo().title + "`\n" +
                                "\uD83D\uDCB0 Autor: `" + track.getInfo().author + "`\n" +
                                "\uD83D\uDCE2 Tipo de vídeo: `" +
                                (track.getInfo().isStream ? "Stream" : track.getInfo().title.contains("Podcast") ?
                                        "Podcast" : "Música") + "`\n" +
                                "\uD83D\uDCCC Link: [Clique aqui](" + track.getInfo().uri + ")");

        if (searchType == TrackManager.SearchType.SIMPLE_SEARCH) {
            Logger.log("The player " + Utilities.INSTANCE.getFullName(member.getUser()) + " added a music");
            channel.sendMessage(embed.build()).queue();

            StatsController.get().getStats("Tocar Música").suplyStats(1);
            StatsController.get().getStats("Requests Externos").suplyStats(1);
        }

        TrackManager.get().audio = member.getVoiceState().getChannel();
        TrackManager.get().play(track, member);
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        if (playlist.getSelectedTrack() != null) trackLoaded(playlist.getSelectedTrack());
        else if (playlist.isSearchResult()) trackLoaded(playlist.getTracks().get(0));

        else {
            if (TrackManager.get().player.isPaused()) TrackManager.get().player.setPaused(false);

            int limit = Utilities.INSTANCE.isPrime(member) || Utilities.INSTANCE.isDJ(member, null, false) ? 100 : 25;
            int maxMusics = Math.min(playlist.getTracks().size(), limit);

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("💿 " + Utilities.INSTANCE.getFullName(member.getUser()) + " adicionou " + maxMusics + " músicas a fila")
                    .setDescription("\uD83D\uDCBD Informações da playlist:\n\n" +
                            "\ud83d\udcc0 Nome: `" + playlist.getName() + "`\n" +
                            "\uD83C\uDFB6 Músicas: `" + maxMusics + "`\n" +
                            "\uD83D\uDCCC Link: [Clique aqui](" + trackUrl + ")");

            Logger.log("The player " + Utilities.INSTANCE.getFullName(member.getUser()) + " added a playlist with " + maxMusics + " musics");

            TrackManager.get().audio = member.getVoiceState().getChannel();
            TaskHelper.runAsync(() -> {
                for (int i = 0; i < maxMusics; i++) {
                    AudioTrack track = playlist.getTracks().get(i);

                    if (track.getInfo().title != null) {

                        if (!permitedTrack(track)) continue;
                        TrackManager.get().play(track, member);

                    } else {

                        String link = "https://youtube.com/watch?v=" + track.getIdentifier();
                        TrackManager.get().loadTrack(link, member, channel, TrackManager.SearchType.LOOKING_PLAYLIST);

                    }
                }
            });

            StatsController.get().getStats("Tocar Música").suplyStats(maxMusics);
            StatsController.get().getStats("Requests Externos").suplyStats(maxMusics);
            channel.sendMessage(embed.build()).queue();
        }
    }

    @Override
    public void noMatches() {
        if (searchType == TrackManager.SearchType.SIMPLE_SEARCH)
            channel.sendMessage("**Erro** \uD83D\uDCCC `Não encontrei nada relacionado a busca`").queue();
    }

    @Override
    public void loadFailed(FriendlyException exception) {
        if (searchType == TrackManager.SearchType.SIMPLE_SEARCH) {
            channel.sendMessage("**Erro** \uD83D\uDCCC `O vídeo ou playlist está privado`").queue();
            Logger.error(exception);
        }
    }

    private boolean permitedTrack(AudioTrack track) {
        // duration limit
        return Math.round(track.getDuration() / 1000.0) <= TimeUnit.MINUTES.toSeconds(20)

                //block animal sounds '-'
                && !track.getInfo().title.toLowerCase().contains("som do")
                && !track.getInfo().title.toLowerCase().contains("som de")
                && !track.getInfo().title.toLowerCase().contains("som da");
    }
}
