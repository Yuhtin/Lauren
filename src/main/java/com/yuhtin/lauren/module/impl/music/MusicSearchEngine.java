package com.yuhtin.lauren.module.impl.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.yuhtin.lauren.module.Module;
import com.yuhtin.lauren.module.impl.player.module.PlayerModule;
import com.yuhtin.lauren.util.EmbedUtil;
import com.yuhtin.lauren.util.LoggerUtil;
import com.yuhtin.lauren.util.TaskHelper;
import lombok.Builder;
import lombok.Data;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

@Builder
@Data
public class MusicSearchEngine implements AudioLoadResultHandler {

    private final GuildedMusicPlayer player;
    private final String trackUrl;
    private final Member member;
    private final Message message;
    private final MusicSearchType searchType;

    @Override
    public void trackLoaded(AudioTrack track) {
        // TODO: statsController.getStats("Requests Externos").suplyStats(1);

        String podcastMessage = track.getInfo().title.contains("Podcast") ? "Podcast" : "Música";
        String videoType = track.getInfo().isStream ? "Stream" : podcastMessage;

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("💿 " + member.getUser().getName() + " adicionou 1 música a fila")
                .setColor(EmbedUtil.getColor())
                .setDescription(
                        "\ud83d\udcc0 Nome: `" + track.getInfo().title + "`\n" +
                                "\uD83D\uDCB0 Autor: `" + track.getInfo().author + "`\n" +
                                "\uD83D\uDCE2 Tipo de vídeo: `" + videoType + "`\n" +
                                "\uD83D\uDCCC Link: [Clique aqui](" + track.getInfo().uri + ")");

        if (message != null) {
            message.replyEmbeds(embed.build()).queue();
        }

        // TODO: statsController.getStats("Tocar Música").suplyStats(1);

        player.setAudioChannel(member.getVoiceState().getChannel());
        player.play(track, member);
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        if (playlist.getSelectedTrack() != null) trackLoaded(playlist.getSelectedTrack());
        else if (playlist.isSearchResult()) trackLoaded(playlist.getTracks().get(0));
        else {

            PlayerModule playerModule = Module.instance(PlayerModule.class);
            int limit = playerModule == null ? 25 : getLimit(playerModule, member);
            int maxMusics = Math.min(playlist.getTracks().size(), limit);

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("💿 " + member.getUser().getName() + " adicionou " + maxMusics + " músicas a fila")
                    .setDescription("\uD83D\uDCBD Informações da playlist:\n\n" +
                            "\ud83d\udcc0 Nome: `" + playlist.getName() + "`\n" +
                            "\uD83C\uDFB6 Músicas: `" + maxMusics + "`\n" +
                            "\uD83D\uDCCC Link: [Clique aqui](" + trackUrl + ")");

            LoggerUtil.getLogger().info("The player " + member.getUser().getName() + " added a playlist with " + maxMusics + " musics");

            player.setAudioChannel(member.getVoiceState().getChannel());

            TaskHelper.runAsync(() -> {
                for (int i = 0; i < maxMusics; i++) {
                    val track = playlist.getTracks().get(i);

                    if (track.getInfo().title != null) {
                        player.play(track, member);
                    } else {
                        val link = "https://youtube.com/watch?v=" + track.getIdentifier();

                        MusicModule musicModule = Module.instance(MusicModule.class);
                        if (musicModule != null) {
                            musicModule.loadTrack(link, member, null, MusicSearchType.LOOKING_PLAYLIST);
                        }
                    }
                }
            });

            message.replyEmbeds(embed.build()).queue();
        }
    }

    private int getLimit(PlayerModule playerModule, Member member) {
        return playerModule.isPrime(member) || playerModule.isDJ(member) ? 100 : 25;
    }

    @Override
    public void noMatches() {
        if (message == null) return;
        message.reply("**Erro** \uD83D\uDCCC `Não encontrei nada relacionado a busca`").queue();
    }

    @Override
    public void loadFailed(FriendlyException exception) {
        LoggerUtil.printException(exception);

        if (message == null) return;
        message.reply("**Erro** \uD83D\uDCCC `O vídeo ou playlist está privado`").queue();
    }

}
