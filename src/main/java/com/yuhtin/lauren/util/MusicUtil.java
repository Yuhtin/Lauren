package com.yuhtin.lauren.util;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.yuhtin.lauren.module.impl.music.AudioInfo;
import com.yuhtin.lauren.module.impl.music.GuildedMusicPlayer;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;

public class MusicUtil {

    public static boolean isInVoiceChannel(Member member) {
        return member.getVoiceState() != null && member.getVoiceState().getChannel() != null;
    }

    public static boolean isMusicOwner(Member member, GuildedMusicPlayer player) {
        AudioInfo trackInfo = player.getTrackInfo();
        if (trackInfo == null) return false;

        return trackInfo.getAuthorId() == member.getIdLong();
    }

    public static String getProgressBar(AudioTrack info) {
        double percent = (double) info.getPosition() / info.getInfo().length;
        int progressBars = (int) (10 * percent);

        return getTimeStamp(info.getPosition()) + " / " + getTimeStamp(info.getInfo().length) + " [" +
                "─".repeat(Math.max(0, progressBars)) +
                "◯" +
                "─".repeat(Math.max(0, 10 - (progressBars + 1))) +
                "](" + info.getInfo().uri + ")";
    }

    public static boolean isIdle(GuildedMusicPlayer player, InteractionHook hook) {
        if (player == null || player.getPlayer().getPlayingTrack() == null) {
            hook.sendMessageEmbeds(EmbedUtil.create("Eita não tem nenhuma música tocando, adiciona umas ai <3")).queue();
            return true;
        }

        return false;
    }

    public static boolean isInSameChannel(Member member, InteractionHook hook) {
        AudioManager audioManager = member.getGuild().getAudioManager();
        if (audioManager.getConnectedChannel() == null) return true;

        GuildVoiceState voiceState = member.getVoiceState();
        if (voiceState != null
                && voiceState.getChannel() != null
                && voiceState.getChannel().getIdLong() == audioManager.getConnectedChannel().getIdLong()) {
            return true;
        }

        hook.sendMessage("\uD83C\uDFB6 Amiguinho, você precisa estar no mesmo canal de voz que eu para usar esse comando!").queue();
        return false;
    }

    public static EmbedBuilder showTrackInfo(AudioTrack currentTrack, GuildedMusicPlayer player) {
        AudioInfo trackInfo = player.getTrackInfo();
        if (trackInfo == null) return EmbedUtil.of("Eita, não consegui encontrar as informações da música atual!");

        String isRepeating = trackInfo.isRepeat() ? "`Ativa`" : "`Desativada`";
        AudioTrackInfo audioInfo = currentTrack.getInfo();

        String contentType = audioInfo.isStream ? "Live" :
                audioInfo.title.contains("Podcast") ? "Podcast" : "Música";

        String statusIcon = player.isPaused() ? "⏯️" : "⏸";

        String progressBar = MusicUtil.getProgressBar(currentTrack);

        return new EmbedBuilder()
                .setColor(Color.GREEN)
                .setTitle("\ud83d\udcbf Informações da música atual")
                .setDescription("\ud83d\udcc0 Nome: `" + audioInfo.title + "`\n" +
                        "\uD83D\uDCB0 Autor: `" + audioInfo.author + "`\n" +
                        "\uD83D\uDCE2 Tipo de vídeo: `" + contentType + "`\n" +
                        "<a:infinito:703187274912759899> Repetição: " + isRepeating + "\n" +
                        "\uD83E\uDDEC Membro que adicionou: <@" + trackInfo.getAuthorId() + ">\n" +
                        "\uD83E\uDDEA Timeline: " + statusIcon + " ⏭ \uD83D\uDD0A " + progressBar + "\n" +
                        "\n" +
                        "\uD83D\uDCCC Link: [Clique aqui](" + audioInfo.uri + ")");
    }

    public void forceSkipTrack(GuildedMusicPlayer player) {
        player.onTrackEnd(player.getPlayer(), player.getPlayer().getPlayingTrack(), AudioTrackEndReason.STOPPED);
    }

    public static String getTimeStamp(long milis) {
        var seconds = milis / 1000L;

        val hours = Math.floorDiv(seconds, 3600L);
        seconds -= hours * 3600L;

        val mins = Math.floorDiv(seconds, 60L);
        seconds -= mins * 60L;

        return ((hours == 0L) ? "" : (hours + ":")) + String.format("%02d", mins) + ":" + String.format("%02d", seconds);
    }

}
