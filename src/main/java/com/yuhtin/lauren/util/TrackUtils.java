package com.yuhtin.lauren.util;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.yuhtin.lauren.core.music.TrackManager;
import com.yuhtin.lauren.core.util.UserUtil;
import com.yuhtin.lauren.module.Module;
import com.yuhtin.lauren.module.impl.player.PlayerModule;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.awt.*;

public class TrackUtils {

    public static String getProgressBar(AudioTrack info) {
        double percent = (double) info.getPosition() / info.getInfo().length;
        int progressBars = (int) (10 * percent);

        return getTimeStamp(info.getPosition()) + " / " + getTimeStamp(info.getInfo().length) + " [" +
                "─".repeat(Math.max(0, progressBars)) +
                "◯" +
                "─".repeat(Math.max(0, 10 - (progressBars + 1))) +
                "](" + info.getInfo().uri + ")";
    }

    public static boolean isInMusicChannel(Member member) {
        return member.getVoiceState() != null && member.getVoiceState().getChannel().getType() != null;
    }

    public static boolean isMusicOwner(Member member) {
        return TrackManager.of(member.getGuild())
                .getTrackInfo()
                .getAuthor()
                .equals(member);
    }

    public static boolean isIdle(Guild guild, InteractionHook hook) {
        if (TrackManager.of(guild).getPlayer().getPlayingTrack() == null) {
            hook.sendMessageEmbeds(EmbedUtil.of("Eita não tem nenhum batidão tocando, adiciona uns ai <3")).queue();
            return true;
        }

        return false;
    }

    public static EmbedBuilder showTrackInfo(AudioTrack currentTrack, TrackManager trackManager) {
        val isRepeating = trackManager.getTrackInfo().isRepeat() ? "`Ativa`" : "`Desativada`";

        return new EmbedBuilder()
                .setColor(Color.GREEN)
                .setTitle("\ud83d\udcbf Informações da música atual")
                .setThumbnail("https://img.youtube.com/vi/" + currentTrack.getIdentifier() + "/0.jpg")
                .setDescription("\ud83d\udcc0 Nome: `" + currentTrack.getInfo().title + "`\n" +
                        "\uD83D\uDCB0 Autor: `" + currentTrack.getInfo().author + "`\n" +
                        "\uD83D\uDCE2 Tipo de vídeo: `" + (currentTrack.getInfo().isStream ? "Stream" : currentTrack.getInfo().title.contains("Podcast") ? "Podcast" : "Música") + "`\n" +
                        "<a:infinito:703187274912759899> Repetição: " + isRepeating + "\n" +
                        "\uD83E\uDDEC Membro que adicionou: <@" + trackManager.getTrackInfo().getAuthor().getIdLong() + ">\n" +
                        "\uD83E\uDDEA Timeline: " + (trackManager.getPlayer().isPaused() ? "⏯️" : "⏸") + " ⏭ \uD83D\uDD0A " + TrackUtils.getProgressBar(currentTrack) + "\n" +
                        "\n" +
                        "\uD83D\uDCCC Link: [Clique aqui](" + currentTrack.getInfo().uri + ")");
    }

    //public void forceSkipTrack() { TrackManager.get().musicManager.scheduler.onTrackEnd(true); }

    public static String getTimeStamp(long milis) {
        var seconds = milis / 1000L;

        val hours = Math.floorDiv(seconds, 3600L);
        seconds -= hours * 3600L;

        val mins = Math.floorDiv(seconds, 60L);
        seconds -= mins * 60L;

        return ((hours == 0L) ? "" : (hours + ":")) + String.format("%02d", mins) + ":" + String.format("%02d", seconds);
    }

}
