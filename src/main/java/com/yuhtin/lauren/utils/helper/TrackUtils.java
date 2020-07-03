package com.yuhtin.lauren.utils.helper;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.yuhtin.lauren.core.music.AudioInfo;

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

    public static String buildQueueMessage(AudioInfo info) {
        AudioTrackInfo trackInfo = info.getTrack().getInfo();
        String title = trackInfo.title;
        long length = trackInfo.length;

        return "`[ " + getTimeStamp(length) + " ]` " + title + " (<@" + info.getAuthor().getIdLong() + ">)\n";
    }

    public static String getTimeStamp(long milis) {
        long seconds = milis / 1000L;
        final long hours = Math.floorDiv(seconds, 3600L);
        seconds -= hours * 3600L;
        final long mins = Math.floorDiv(seconds, 60L);
        seconds -= mins * 60L;

        return ((hours == 0L) ? "" : (hours + ":")) + String.format("%02d", mins) + ":" + String.format("%02d", seconds);
    }

}
