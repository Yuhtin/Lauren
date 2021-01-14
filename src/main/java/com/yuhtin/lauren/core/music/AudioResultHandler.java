package com.yuhtin.lauren.core.music;

import com.google.inject.Inject;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.statistics.StatsController;
import com.yuhtin.lauren.models.enums.LogType;
import com.yuhtin.lauren.utils.helper.TaskHelper;
import com.yuhtin.lauren.utils.helper.Utilities;
import lombok.Builder;
import lombok.Data;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

@Builder
@Data
public final class AudioResultHandler implements AudioLoadResultHandler {

    @Inject private static Logger logger;
    @Inject private static StatsController statsController;

    private final TrackManager trackManager;
    private final String trackUrl;
    private final Member member;
    private final TextChannel channel;
    private final TrackManager.SearchType searchType;

    @Override
    public void trackLoaded(AudioTrack track) {

        if (!permitedTrack(track, Utilities.INSTANCE.isDJ(member, null, false))) {

            if (searchType == TrackManager.SearchType.SIMPLE_SEARCH)
                channel.sendMessage("<:rindo_de_voce:751941649655136588>" +
                        " Sua música foi bloqueada por ser muito grande ou por conter o nome de algum animal")
                        .queue();
            return;

        }

        String podcastMessage = track.getInfo().title.contains("Podcast") ? "Podcast" : "Música";
        String videoType = track.getInfo().isStream ? "Stream" : podcastMessage;

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("💿 " + Utilities.INSTANCE.getFullName(member.getUser()) + " adicionou 1 música a fila")
                .setDescription(
                        "\ud83d\udcc0 Nome: `" + track.getInfo().title + "`\n" +
                                "\uD83D\uDCB0 Autor: `" + track.getInfo().author + "`\n" +
                                "\uD83D\uDCE2 Tipo de vídeo: `" + videoType + "`\n" +
                                "\uD83D\uDCCC Link: [Clique aqui](" + track.getInfo().uri + ")");

        if (searchType == TrackManager.SearchType.SIMPLE_SEARCH) {

            channel.sendMessage(embed.build()).queue();

            statsController.getStats("Tocar Música").suplyStats(1);
            statsController.getStats("Requests Externos").suplyStats(1);

        }

        this.trackManager.setAudio(member.getVoiceState().getChannel());
        this.trackManager.play(track, member);

    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        if (playlist.getSelectedTrack() != null) trackLoaded(playlist.getSelectedTrack());
        else if (playlist.isSearchResult()) trackLoaded(playlist.getTracks().get(0));

        else {

            int limit = Utilities.INSTANCE.isPrime(member) || Utilities.INSTANCE.isDJ(member, null, false) ? 100 : 25;
            int maxMusics = Math.min(playlist.getTracks().size(), limit);

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("💿 " + Utilities.INSTANCE.getFullName(member.getUser()) + " adicionou " + maxMusics + " músicas a fila")
                    .setDescription("\uD83D\uDCBD Informações da playlist:\n\n" +
                            "\ud83d\udcc0 Nome: `" + playlist.getName() + "`\n" +
                            "\uD83C\uDFB6 Músicas: `" + maxMusics + "`\n" +
                            "\uD83D\uDCCC Link: [Clique aqui](" + trackUrl + ")");

            logger.info("The player " + Utilities.INSTANCE.getFullName(member.getUser()) + " added a playlist with " + maxMusics + " musics");

            trackManager.setAudio(member.getVoiceState().getChannel());
            TaskHelper.runAsync(() -> {
                for (int i = 0; i < maxMusics; i++) {
                    AudioTrack track = playlist.getTracks().get(i);

                    if (track.getInfo().title != null) {

                        if (!permitedTrack(track, Utilities.INSTANCE.isDJ(member, null, false))) continue;
                        this.trackManager.play(track, member);

                    } else {

                        String link = "https://youtube.com/watch?v=" + track.getIdentifier();
                        this.trackManager.loadTrack(link, member, channel, TrackManager.SearchType.LOOKING_PLAYLIST);

                    }
                }
            });

            statsController.getStats("Tocar Música").suplyStats(maxMusics);
            statsController.getStats("Requests Externos").suplyStats(maxMusics);
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
            logger.log(LogType.WARNING, "Error on try load a track", exception);

        }
    }

    private boolean permitedTrack(AudioTrack track, boolean isDj) {
        return (isDj || Math.round(track.getDuration() / 1000.0) <= TimeUnit.MINUTES.toSeconds(30));
    }

}
