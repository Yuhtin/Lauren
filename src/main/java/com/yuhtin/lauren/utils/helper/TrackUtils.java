package com.yuhtin.lauren.utils.helper;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.yuhtin.lauren.core.music.AudioInfo;
import com.yuhtin.lauren.core.music.TrackManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class TrackUtils {

    private static final TrackUtils INSTANCE = new TrackUtils();

    public static TrackUtils get() { return INSTANCE; }

    public String getProgressBar(AudioTrack info) {
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

    public String buildQueueMessage(AudioInfo info) {
        AudioTrackInfo trackInfo = info.getTrack().getInfo();
        String title = trackInfo.title;
        long length = trackInfo.length;

        return "`[" + getTimeStamp(length) + "]` " + title + " (<@" + info.getAuthor().getIdLong() + ">)\n";
    }

    public boolean isInVoiceChannel(Member member) {
        return member.getVoiceState() != null
                && member.getVoiceState().getChannel() != null
                && (member.getVoiceState().getChannel().getName().contains("Batidões")
                || Utilities.INSTANCE.isDJ(member, null, false));
    }

    public boolean isCurrentDj(Member member) {
        return TrackManager.get()
                .getTrackInfo()
                .getAuthor()
                .equals(member);
    }

    public boolean isIdle(TextChannel channel) {
        if (TrackManager.get().player.getPlayingTrack() == null) {
            if (channel != null) channel.sendMessage("\uD83D\uDCCC Eita, não tem nenhum batidão pra tocar, adiciona uns ai <3").queue();
            return true;
        }

        return false;
    }

    public void forceSkipTrack() { TrackManager.get().musicManager.scheduler.onTrackEnd(true); }

    public String getTimeStamp(long milis) {
        long seconds = milis / 1000L;
        final long hours = Math.floorDiv(seconds, 3600L);
        seconds -= hours * 3600L;
        final long mins = Math.floorDiv(seconds, 60L);
        seconds -= mins * 60L;

        return ((hours == 0L) ? "" : (hours + ":")) + String.format("%02d", mins) + ":" + String.format("%02d", seconds);
    }

}
