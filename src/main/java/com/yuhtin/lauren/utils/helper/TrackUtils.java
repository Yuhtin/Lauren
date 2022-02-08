package com.yuhtin.lauren.utils.helper;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.yuhtin.lauren.core.music.TrackManager;
import lombok.val;
import lombok.var;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.InteractionHook;

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

    public boolean isInMusicChannel(Member member) {
        return member.getVoiceState() != null
                && member.getVoiceState().getChannel() != null
                && (member.getVoiceState().getChannel().getName().contains("Batidões")
                || UserUtil.isDJ(member, null));
    }

    public boolean isMusicOwner(Member member) {
        return TrackManager.of(member.getGuild())
                .getTrackInfo()
                .getAuthor()
                .equals(member);
    }

    public boolean isIdle(Guild guild, InteractionHook hook) {
        if (TrackManager.of(guild).getPlayer().getPlayingTrack() == null) {
            hook.sendMessage("\uD83D\uDCCC Eita, não tem nenhum batidão pra tocar, adiciona uns ai <3").queue();
            return true;
        }

        return false;
    }

    //public void forceSkipTrack() { TrackManager.get().musicManager.scheduler.onTrackEnd(true); }

    public String getTimeStamp(long milis) {
        var seconds = milis / 1000L;

        val hours = Math.floorDiv(seconds, 3600L);
        seconds -= hours * 3600L;

        val mins = Math.floorDiv(seconds, 60L);
        seconds -= mins * 60L;

        return ((hours == 0L) ? "" : (hours + ":")) + String.format("%02d", mins) + ":" + String.format("%02d", seconds);
    }

}
