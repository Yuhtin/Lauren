package com.yuhtin.lauren.core.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.yuhtin.lauren.utils.helper.TrackUtils;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.util.HashSet;
import java.util.Set;

public class AudioInfo {

    private final Set<String> skips = new HashSet<>();
    @Getter @Setter private boolean repeat;
    @Getter @Setter private AudioTrack track;
    @Getter private final Member author;

    public AudioInfo(AudioTrack track, Member author) {
        this.track = track;
        this.author = author;
    }

    public int getSkips() {
        return skips.size();
    }

    public void addSkip(User user) {
        skips.add(user.getId());
    }

    public boolean hasVoted(User user) {
        return skips.contains(user.getId());
    }

    @Override
    public String toString() {
        return "`["
                + TrackUtils.get().getTimeStamp(track.getDuration()) +
                "]` **"
                + track.getInfo().title +
                "** - <@"
                + author.getIdLong() +
                ">";
    }

}