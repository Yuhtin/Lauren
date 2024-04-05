package com.yuhtin.lauren.core.music;

import com.google.inject.Inject;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.statistics.StatsController;
import com.yuhtin.lauren.models.enums.LogType;
import com.yuhtin.lauren.util.EmbedUtil;
import com.yuhtin.lauren.util.TaskHelper;
import lombok.Builder;
import lombok.Data;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.util.concurrent.TimeUnit;

@Builder
@Data
public final class AudioResultHandler implements AudioLoadResultHandler {

    @Inject
    private static Logger logger;
    @Inject
    private static StatsController statsController;

    private final TrackManager trackManager;
    private final String trackUrl;
    private final Member member;
    private final Message message;
    private final TrackManager.SearchType searchType;

    @Override
    public void trackLoaded(AudioTrack track) {
        statsController.getStats("Requests Externos").suplyStats(1);

        if (!permitedTrack(track, UserUtil.isDJ(member, null))) {
            if (searchType == TrackManager.SearchType.SIMPLE_SEARCH)
                message.reply("<:rindo_de_voce:751941649655136588>" +
                                " Sua música foi bloqueada por ter mais de 12 minutos.")
                        .queue();
            return;
        }

        String podcastMessage = track.getInfo().title.contains("Podcast") ? "Podcast" : "Música";
        String videoType = track.getInfo().isStream ? "Stream" : podcastMessage;

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("💿 " + member.getUser().getAsTag() + " adicionou 1 música a fila")
                .setColor(EmbedUtil.getColor())
                .setDescription(
                        "\ud83d\udcc0 Nome: `" + track.getInfo().title + "`\n" +
                                "\uD83D\uDCB0 Autor: `" + track.getInfo().author + "`\n" +
                                "\uD83D\uDCE2 Tipo de vídeo: `" + videoType + "`\n" +
                                "\uD83D\uDCCC Link: [Clique aqui](" + track.getInfo().uri + ")");

        if (searchType == TrackManager.SearchType.SIMPLE_SEARCH) {
            message.replyEmbeds(embed.build()).queue();
        }

        statsController.getStats("Tocar Música").suplyStats(1);

        trackManager.setAudio(member.getVoiceState().getChannel());
        trackManager.play(track, member);
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        if (playlist.getSelectedTrack() != null) trackLoaded(playlist.getSelectedTrack());
        else if (playlist.isSearchResult()) trackLoaded(playlist.getTracks().get(0));
        else {

            int limit = UserUtil.isPrime(member) || UserUtil.isDJ(member, null) ? 100 : 25;
            int maxMusics = Math.min(playlist.getTracks().size(), limit);

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("💿 " + member.getUser().getAsTag() + " adicionou " + maxMusics + " músicas a fila")
                    .setDescription("\uD83D\uDCBD Informações da playlist:\n\n" +
                            "\ud83d\udcc0 Nome: `" + playlist.getName() + "`\n" +
                            "\uD83C\uDFB6 Músicas: `" + maxMusics + "`\n" +
                            "\uD83D\uDCCC Link: [Clique aqui](" + trackUrl + ")");

            logger.info("The player " + member.getUser().getAsTag() + " added a playlist with " + maxMusics + " musics");

            trackManager.setAudio(member.getVoiceState().getChannel());
            TaskHelper.runAsync(() -> {
                for (int i = 0; i < maxMusics; i++) {
                    val track = playlist.getTracks().get(i);

                    if (track.getInfo().title != null) {
                        if (!permitedTrack(track, UserUtil.isDJ(member, null))) continue;
                        trackManager.play(track, member);
                    } else {
                        val link = "https://youtube.com/watch?v=" + track.getIdentifier();
                        trackManager.loadTrack(link, member, null, TrackManager.SearchType.LOOKING_PLAYLIST);
                    }
                }
            });

            message.replyEmbeds(embed.build()).queue();
        }
    }

    @Override
    public void noMatches() {
        if (searchType == TrackManager.SearchType.SIMPLE_SEARCH)
            message.reply("**Erro** \uD83D\uDCCC `Não encontrei nada relacionado a busca`").queue();
    }

    @Override
    public void loadFailed(FriendlyException exception) {
        if (searchType == TrackManager.SearchType.SIMPLE_SEARCH) {
            message.reply("**Erro** \uD83D\uDCCC `O vídeo ou playlist está privado`").queue();
            logger.log(LogType.WARNING, "Error on try load a track", exception);
        }
    }

    private boolean permitedTrack(AudioTrack track, boolean isDj) {
        return (isDj || Math.round(track.getDuration() / 1000.0) <= TimeUnit.MINUTES.toSeconds(12));
    }

}
