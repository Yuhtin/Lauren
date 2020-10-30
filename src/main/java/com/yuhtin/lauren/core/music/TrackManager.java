package com.yuhtin.lauren.core.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.yuhtin.lauren.Lauren;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class TrackManager extends AudioEventAdapter {

    private static TrackManager INSTANCE;

    public final GuildMusicManager musicManager;
    public final AudioPlayerManager audioManager;
    public final AudioPlayer player;
    public VoiceChannel audio;

    public TrackManager() {
        this.audioManager = new DefaultAudioPlayerManager();
        this.player = audioManager.createPlayer();

        musicManager = new GuildMusicManager(player);
        AudioSourceManagers.registerRemoteSources(audioManager);
        AudioSourceManagers.registerLocalSource(audioManager);

        player.addListener(this);
        Lauren.getInstance().getGuild().getAudioManager().setSendingHandler(new AudioPlayerSendHandler(player));
    }

    public static TrackManager get() {
        if (INSTANCE == null) INSTANCE = new TrackManager();

        return INSTANCE;
    }

    public void destroy() {
        if (audio == null) return;

        audio.getGuild().getAudioManager().closeAudioConnection();
        purgeQueue();
        player.stopTrack();
        musicManager.player.destroy();
    }

    public void loadTrack(String trackUrl, Member member, TextChannel channel, SearchType type) {
        String emoji = trackUrl.contains("spotify.com") ? "<:spotify:751049445592006707>" : "<:youtube:751031330057486366>";
        if (type == SearchType.SIMPLE_SEARCH) {
            channel.sendMessage(emoji + " **Procurando** 🔎 `" + trackUrl.replace("ytsearch: ", "") + "`").queue();
            channel.sendTyping().queue();
        }

        audioManager.loadItemOrdered(musicManager, trackUrl, AudioResultHandler.builder()
                .trackUrl(trackUrl)
                .member(member)
                .channel(channel)
                .searchType(type)
                .build());
    }

    public void play(AudioTrack track, Member member) {
        musicManager.scheduler.queue(track, member);
    }

    public void shuffleQueue() {
        List<AudioInfo> tempQueue = new ArrayList<>(this.getQueuedTracks());

        AudioInfo current = tempQueue.get(0);
        tempQueue.remove(0);

        Collections.shuffle(tempQueue);
        tempQueue.add(0, current);

        purgeQueue();
        musicManager.scheduler.queue.addAll(tempQueue);
    }

    public Set<AudioInfo> getQueuedTracks() {
        return new LinkedHashSet<>(musicManager.scheduler.queue);
    }

    public void purgeQueue() {
        musicManager.scheduler.queue.clear();
    }

    public AudioInfo getTrackInfo() {
        return musicManager.scheduler.queue
                .stream()
                .filter(audioInfo -> audioInfo.getTrack().equals(player.getPlayingTrack()))
                .findFirst()
                .orElse(null);
    }

    private boolean permitedTrack(AudioTrack track) {
        // duration limit
        return Math.round(track.getDuration() / 1000.0) <= TimeUnit.MINUTES.toSeconds(20)

                //block animal sounds '-'
                && !track.getInfo().title.toLowerCase().contains("som do")
                && !track.getInfo().title.toLowerCase().contains("som de")
                && !track.getInfo().title.toLowerCase().contains("som da");
    }

    public enum SearchType {
        LOOKING_PLAYLIST,
        SIMPLE_SEARCH
    }
}