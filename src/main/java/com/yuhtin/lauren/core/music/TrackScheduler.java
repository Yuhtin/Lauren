package com.yuhtin.lauren.core.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {
    public final BlockingQueue<AudioInfo> queue;
    private final AudioPlayer player;
    private final Guild guild;

    public TrackScheduler(AudioPlayer player, Guild guild) {
        this.player = player;
        this.guild = guild;
        this.queue = new LinkedBlockingQueue<>();
    }

    public void queue(AudioTrack track, Member author) {

        AudioInfo info = new AudioInfo(track, author);
        queue.add(info);

        if (this.player.getPlayingTrack() == null) this.player.playTrack(track);

    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        VoiceChannel voice = TrackManager.of(guild).getAudio();
        voice.getGuild().getAudioManager().openAudioConnection(voice);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        AudioInfo head = queue.peek();
        if (head == null) return;

        // repeat music system
        if (endReason == AudioTrackEndReason.FINISHED && head.isRepeat()) {
            head.setRepeat(false);

            AudioTrack audioTrack = head.getTrack().makeClone();
            head.setTrack(audioTrack);
            player.playTrack(audioTrack);
            return;
        }

        // remove head
        queue.remove();

        Guild guild = head.getAuthor().getGuild();
        if (queue.isEmpty()) {
            guild.getAudioManager().closeAudioConnection();
            return;
        }

        // play next track (actual head)
        player.playTrack(queue.element().getTrack());
    }

}