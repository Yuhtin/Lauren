package com.yuhtin.lauren.core.music;

import com.sedmelluq.discord.lavaplayer.filter.equalizer.EqualizerFactory;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.yuhtin.lauren.Lauren;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.util.*;
import java.util.List;

public class TrackManager extends AudioEventAdapter {

    private static final float[] BASS_BOOST = {
            0.2f, 0.15f, 0.1f,
            0.05f, 0.0f, -0.05f,
            -0.1f, -0.1f, -0.1f,
            -0.1f, -0.1f, -0.1f,
            -0.1f, -0.1f, -0.1f
    };

    private static TrackManager instance;

    public final GuildMusicManager musicManager;
    public final AudioPlayerManager audioManager;
    @Getter private final EqualizerFactory equalizer;
    public final AudioPlayer player;
    public VoiceChannel audio;

    public TrackManager() {
        this.audioManager = new DefaultAudioPlayerManager();
        this.player = audioManager.createPlayer();
        this.equalizer = new EqualizerFactory();

        musicManager = new GuildMusicManager(player);
        AudioSourceManagers.registerRemoteSources(audioManager);
        AudioSourceManagers.registerLocalSource(audioManager);

        player.addListener(this);
        Lauren.getInstance().getGuild().getAudioManager().setSendingHandler(new AudioPlayerSendHandler(player));

        player.setFilterFactory(this.equalizer);
    }

    public static TrackManager get() {
        if (instance == null) instance = new TrackManager();

        return instance;
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

    public void eqHighBass(float diff) {
        player.setFilterFactory(equalizer);
        for (int i = 0; i < BASS_BOOST.length; i++) {
            equalizer.setGain(i, BASS_BOOST[i] + diff);
        }
    }

    public void eqLowBass(float diff) {
        player.setFilterFactory(equalizer);
        for (int i = 0; i < BASS_BOOST.length; i++) {
            equalizer.setGain(i, -BASS_BOOST[i] + diff);
        }
    }

    public void bassBoost() {
        player.setFilterFactory(equalizer);
        for (int i = 0; i < BASS_BOOST.length; i++) {
            equalizer.setGain(i, BASS_BOOST[i] + 0.12f);
        }

        for (int i = 0; i < BASS_BOOST.length; i++) {
            equalizer.setGain(i, -BASS_BOOST[i] + 0.013f);
        }
    }

    public enum SearchType {
        LOOKING_PLAYLIST,
        SIMPLE_SEARCH
    }
}