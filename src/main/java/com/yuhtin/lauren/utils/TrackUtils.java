package com.yuhtin.lauren.utils;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.yuhtin.lauren.core.music.TrackManager;
import lombok.val;
import lombok.var;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.awt.*;

public class TrackUtils {

    public static String getProgressBar(AudioTrack info) {
        double percent = (double) info.getPosition() / info.getInfo().length;
        int progressBars = (int) (10 * percent);

        StringBuilder builder = new StringBuilder();
        builder.append(getTimeStamp(info.getPosition())).append(" / ").append(getTimeStamp(info.getInfo().length)).append(" [");

        for (int i = 0; i < progressBars; i++) {
            builder.append("─");
        }

        builder.append("◯");

        for (int i = progressBars + 1; i < 10; i++) {
            builder.append("─");
        }

        builder.append("](").append(info.getInfo().uri).append(")");

        return builder.toString();
    }

    public static boolean isInMusicChannel(Member member) {
        return member.getVoiceState() != null
                && member.getVoiceState().getChannel() != null
                && (member.getVoiceState().getChannel().getName().contains("Batidões")
                || UserUtil.isDJ(member, null));
    }

    public static boolean isMusicOwner(Member member) {
        return TrackManager.of(member.getGuild())
                .getTrackInfo()
                .getAuthor()
                .equals(member);
    }

    public static boolean isIdle(Guild guild, InteractionHook hook) {
        if (TrackManager.of(guild).getPlayer().getPlayingTrack() == null) {
            hook.sendMessage("\uD83D\uDCCC Eita, não tem nenhum batidão pra tocar, adiciona uns ai <3").queue();
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
