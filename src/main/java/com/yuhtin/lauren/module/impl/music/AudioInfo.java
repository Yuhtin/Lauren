package com.yuhtin.lauren.module.impl.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.yuhtin.lauren.util.MusicUtil;
import lombok.Data;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.util.HashSet;
import java.util.Set;

@Data
public class AudioInfo {

    private final Set<String> skips = new HashSet<>();
    private AudioTrack track;
    private final String authorName;
    private final long authorId;

    public AudioInfo(AudioTrack track, Member author) {
        this.track = track;
        this.authorId = author.getIdLong();
        this.authorName = author.getUser().getName();
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
        return "`[" + MusicUtil.getTimeStamp(track.getDuration()) + "]` **" + track.getInfo().title + "** - <@" + authorId + ">";
    }

}