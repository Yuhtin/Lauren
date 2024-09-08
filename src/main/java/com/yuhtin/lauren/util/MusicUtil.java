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

    public static EmbedBuilder buildTrackInfo(AudioTrack currentTrack, GuildedMusicPlayer player) {
        AudioInfo trackInfo = player.getTrackInfo();
        if (trackInfo == null) return EmbedUtil.createDefaultEmbed("Nenhuma música tocando!");

        AudioTrackInfo audioInfo = currentTrack.getInfo();

        String pausedInfo = player.isPaused() ? "⏸️ Musica pausada!" : "";

        String progressBar = MusicUtil.getProgressBar(currentTrack);

        return EmbedUtil.createDefaultEmbed(pausedInfo +
                        "👤 Autor: `" + audioInfo.author + "`\n" +
                        "⏳ Timeline: \uD83D\uDD0A " + progressBar)
                .setColor(Color.GREEN)
                .setTitle("🎵 " + audioInfo.title, audioInfo.uri)
                .setFooter("Adicionador por " + trackInfo.getAuthorName());
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
