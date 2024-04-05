package com.yuhtin.lauren.core.music;

import com.sedmelluq.discord.lavaplayer.filter.equalizer.EqualizerFactory;
import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.getyarn.GetyarnAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.yuhtin.lauren.startup.Startup;
import com.yuhtin.lauren.util.TrackUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.val;
import net.dv8tion.jda.api.audio.SpeakingMode;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;

@EqualsAndHashCode(callSuper = true)
@Data
public class TrackManager extends AudioEventAdapter {

    @Getter
    private static final Map<Long, TrackManager> guildTrackManagers = new HashMap<>();

    private static final float[] BASS_BOOST = {
            0.2f, 0.15f, 0.1f,
            0.05f, 0.0f, -0.05f,
            -0.1f, -0.1f, -0.1f,
            -0.1f, -0.1f, -0.1f,
            -0.1f, -0.1f, -0.1f
    };

    private long guildId;
    private EqualizerFactory equalizer;
    private GuildMusicManager musicManager;
    private AudioPlayerManager audioManager;
    private AudioPlayer player;
    private AudioChannel audio;

    private long lastInfoMessageId;
    private TextChannel textChannel;

    public static TrackManager of(Guild guild) {
        if (guildTrackManagers.containsKey(guild.getIdLong())) return guildTrackManagers.get(guild.getIdLong());


        TrackManager trackManager = new TrackManager();
        trackManager.setGuildId(guild.getIdLong());

        val audioManager = new DefaultAudioPlayerManager();
        audioManager.setItemLoaderThreadPoolSize(128);
        audioManager.getConfiguration().setResamplingQuality(AudioConfiguration.ResamplingQuality.HIGH);
        audioManager.registerSourceManager(new YoutubeAudioSourceManager());
        audioManager.registerSourceManager(new BandcampAudioSourceManager());
        audioManager.registerSourceManager(new BeamAudioSourceManager());
        audioManager.registerSourceManager(new GetyarnAudioSourceManager());
        audioManager.registerSourceManager(new HttpAudioSourceManager());
        audioManager.registerSourceManager(new LocalAudioSourceManager());
        audioManager.registerSourceManager(SoundCloudAudioSourceManager.createDefault());
        audioManager.registerSourceManager(new TwitchStreamAudioSourceManager());
        audioManager.registerSourceManager(new VimeoAudioSourceManager());

        trackManager.setAudioManager(audioManager);
        trackManager.setPlayer(trackManager.getAudioManager().createPlayer());
        trackManager.setMusicManager(new GuildMusicManager(trackManager.getPlayer(), guild));

        AudioSourceManagers.registerRemoteSources(trackManager.getAudioManager());
        AudioSourceManagers.registerLocalSource(trackManager.getAudioManager());

        trackManager.getPlayer().addListener(trackManager);

        val discordAudio = guild.getAudioManager();
        discordAudio.setSendingHandler(new AudioPlayerSendHandler(trackManager.getPlayer()));
        //discordAudio.setReceivingHandler(new AudioBridge(trackManager.getPlayer()));
        discordAudio.setAutoReconnect(true);
        discordAudio.setSelfDeafened(true);
        discordAudio.setSpeakingMode(SpeakingMode.PRIORITY);

        trackManager.setEqualizer(new EqualizerFactory());
        trackManager.getPlayer().setFilterFactory(trackManager.getEqualizer());

        guildTrackManagers.put(guild.getIdLong(), trackManager);

        return trackManager;
    }

    public void setAudio(AudioChannel audio) {
        if (this.audio == audio) return;

        this.audio = audio;

        Guild guild = Startup.getLauren().getBot().getGuildById(guildId);
        if (guild != null && this.audio != null) guild.getAudioManager().openAudioConnection(this.audio);
    }

    public void destroy() {
        guildTrackManagers.remove(guildId);

        if (audio != null) {
            audio.getGuild().getAudioManager().closeAudioConnection();
        }

        purgeQueue();
        player.stopTrack();
        musicManager.getPlayer().destroy();
    }

    public void loadTrack(String trackUrl, Member member, InteractionHook hook, SearchType type) {
        Startup.getLauren().getLogger().info("Loading track " + trackUrl + " [" + type + "] requested by " + member.getUser().getAsTag());
        val emoji = trackUrl.contains("spotify.com") ? "<:spotify:751049445592006707>" : "<:youtube:751031330057486366>";
        val handlerBuilder = AudioResultHandler.builder()
                .trackManager(this)
                .trackUrl(trackUrl)
                .member(member)
                .searchType(type);

        if (type == SearchType.SIMPLE_SEARCH && hook != null) {
            hook.sendMessage(emoji + " **Procurando** 🔎 `" + trackUrl.replace("ytsearch: ", "") + "`").queue(message -> {
                handlerBuilder.message(message);
                audioManager.loadItemOrdered(musicManager, trackUrl, handlerBuilder.build());
            });
            return;
        }

        audioManager.loadItemOrdered(musicManager, trackUrl, handlerBuilder.build());
    }

    public void play(AudioTrack track, Member member) {
        musicManager.getScheduler().queue(track, member);
    }

    public void shuffleQueue() {
        List<AudioInfo> tempQueue = new ArrayList<>(this.getQueuedTracks());

        AudioInfo current = tempQueue.get(0);
        tempQueue.remove(0);

        Collections.shuffle(tempQueue);
        tempQueue.add(0, current);

        purgeQueue();
        musicManager.getScheduler().getQueue().addAll(tempQueue);
    }

    public Set<AudioInfo> getQueuedTracks() {
        return new LinkedHashSet<>(musicManager.getScheduler().getQueue());
    }

    public void purgeQueue() {
        musicManager.getScheduler().getQueue().clear();
    }

    public AudioInfo getTrackInfo() {
        return musicManager.getScheduler().getQueue()
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

    public void skipTrack() {
        player.stopTrack();
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        if (textChannel == null) return;

        tryDeleteLastMessage();

        val embedBuilder = TrackUtils.showTrackInfo(track, this);
        textChannel.sendMessageEmbeds(embedBuilder.build()).queue(message -> lastInfoMessageId = message.getIdLong());
    }

    public void tryDeleteLastMessage() {
        if (textChannel == null || lastInfoMessageId == 0) return;

        textChannel.deleteMessageById(lastInfoMessageId).queue();
        lastInfoMessageId = 0;
    }

    public File downloadAudio() {
        val guild = Startup.getLauren().getBot().getGuildById(guildId);
        if (guild == null) return null;

        val audio = guild.getAudioManager();
        val receivingHandler = (AudioBridge) audio.getReceivingHandler();
        if (receivingHandler == null) return null;

        val tempFile = new File("./temp/", "temp.mp3");
        if (tempFile.exists()) tempFile.delete();

        tempFile.getParentFile().mkdirs();

        try {
            int size = 0;

            List<byte[]> rescievedBytes = new ArrayList<>(receivingHandler.bridgeQueue);
            for (byte[] bs : rescievedBytes) {
                size += bs.length;
            }

            byte[] decodedData = new byte[size];
            int i = 0;
            for (byte[] bs : rescievedBytes) {
                for (byte b : bs) {
                    decodedData[i++] = b;
                }
            }

            getWavFile(tempFile, decodedData);
            return tempFile;
        } catch (IOException | OutOfMemoryError throwable) {
            throwable.printStackTrace();
            return null;
        }
    }

    private void getWavFile(File outFile, byte[] decodedData) throws IOException {
        AudioFormat format = new AudioFormat(8000, 16, 1, true, false);
        AudioSystem.write(new AudioInputStream(new ByteArrayInputStream(decodedData), format, decodedData.length), AudioFileFormat.Type.WAVE, outFile);
    }

    public enum SearchType {
        LOOKING_PLAYLIST,
        SIMPLE_SEARCH
    }
}