package com.yuhtin.lauren.core.music;

import com.sedmelluq.discord.lavaplayer.filter.equalizer.EqualizerFactory;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.yuhtin.lauren.LaurenStartup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.sharding.ShardManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class TrackManager extends AudioEventAdapter {

    @Getter private static final Map<Long, TrackManager> guildTrackManagers = new HashMap<>();

    private static final float[] BASS_BOOST = {
            0.2f, 0.15f, 0.1f,
            0.05f, 0.0f, -0.05f,
            -0.1f, -0.1f, -0.1f,
            -0.1f, -0.1f, -0.1f,
            -0.1f, -0.1f, -0.1f
    };

    private EqualizerFactory equalizer;
    private GuildMusicManager musicManager;
    private AudioPlayerManager audioManager;
    private AudioPlayer player;
    private VoiceChannel audio;

    public static TrackManager of(Guild guild) {

        if (guildTrackManagers.containsKey(guild.getIdLong())) return guildTrackManagers.get(guild.getIdLong());

        TrackManager trackManager = new TrackManager();

        trackManager.setAudioManager(new DefaultAudioPlayerManager());
        trackManager.setPlayer(trackManager.getAudioManager().createPlayer());
        trackManager.setMusicManager(new GuildMusicManager(trackManager.getPlayer()));

        AudioSourceManagers.registerRemoteSources(trackManager.getAudioManager());
        AudioSourceManagers.registerLocalSource(trackManager.getAudioManager());

        trackManager.getPlayer().addListener(trackManager);
        guild.getAudioManager().setSendingHandler(new AudioPlayerSendHandler(trackManager.getPlayer()));

        trackManager.setEqualizer(new EqualizerFactory());
        trackManager.getPlayer().setFilterFactory(trackManager.getEqualizer());

        guildTrackManagers.put(guild.getIdLong(), trackManager);

        return trackManager;

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
                .trackManager(this)
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