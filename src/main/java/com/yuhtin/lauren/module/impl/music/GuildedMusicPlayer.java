package com.yuhtin.lauren.module.impl.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.yuhtin.lauren.module.Module;
import com.yuhtin.lauren.module.impl.music.customplaylist.CustomPlaylist;
import com.yuhtin.lauren.module.impl.music.customplaylist.PlaylistTrackInfo;
import com.yuhtin.lauren.util.FutureBuilder;
import com.yuhtin.lauren.util.LoggerUtil;
import com.yuhtin.lauren.util.MusicUtil;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.Nullable;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Getter
public class GuildedMusicPlayer extends AudioEventAdapter {

    // Guild ID - Owner of that player
    private final long guildId;

    // Music data related
    private final AudioPlayer player;
    private final BlockingQueue<AudioInfo> playlist = new LinkedBlockingQueue<>();

    private final MusicCustomEqualizer equalizer = new MusicCustomEqualizer();

    // Channels
    @Nullable
    private AudioChannel audioChannel;
    @Setter
    private long lastMusicMessageId;
    @Setter
    private long textChannelId;

    public GuildedMusicPlayer(long guildId, AudioPlayer player) {
        this.guildId = guildId;
        this.player = player;
        this.player.setVolume(20);

        player.addListener(this);
        player.setFilterFactory(equalizer);
    }

    public void play(AudioTrack track, Member member) {
        AudioInfo info = new AudioInfo(track, member);
        playlist.add(info);

        if (player.getPlayingTrack() == null) {
            player.playTrack(track);
        }
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        if (audioChannel == null) return;

        Guild guild = audioChannel.getGuild();
        guild.getAudioManager().openAudioConnection(audioChannel);

        sendPlayingMessage(track, null);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        AudioInfo head = playlist.peek();
        if (head == null) {
            loadFromCustomPlaylist();
            return;
        }

        // remove head
        playlist.remove();

        if (playlist.isEmpty()) {
            loadFromCustomPlaylist();
            return;
        }

        // play next track (actual head)
        player.playTrack(playlist.element().getTrack());
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        LoggerUtil.printException(exception);
    }

    private void loadFromCustomPlaylist() {
        MusicModule musicModule = Module.instance(MusicModule.class);
        if (musicModule == null) {
            destroy();
            return;
        }

        CustomPlaylist customPlaylist = musicModule.getCustomPlaylist(guildId);

        PlaylistTrackInfo playNext = customPlaylist.playNext();
        if (playNext == null) {
            destroy();
            return;
        }

        if (audioChannel == null) {
            destroy();
            return;
        }

        deleteLastMessage();

        musicModule.loadTrack(
                MusicSearchType.SIMPLE_SEARCH,
                playNext.trackUrl(),
                audioChannel.getGuild().getSelfMember(),
                null, null
        );
    }

    public void deleteLastMessage() {
        if (audioChannel == null || textChannelId == 0 || lastMusicMessageId == 0) return;

        Guild guild = audioChannel.getGuild();
        TextChannel textChannel = guild.getTextChannelById(textChannelId);
        if (textChannel == null) {
            textChannelId = 0;
            return;
        }

        textChannel.deleteMessageById(lastMusicMessageId).queue();
        lastMusicMessageId = 0;
    }

    public void setAudioChannel(AudioChannel audio) {
        if (this.audioChannel == audio) return;
        else if (this.audioChannel != null) {
            this.audioChannel.getGuild().getAudioManager().closeAudioConnection();
        }

        this.audioChannel = audio;
        if (audio != null) {
            audio.getGuild().getAudioManager().openAudioConnection(audio);
        }
    }

    public void destroy() {
        deleteLastMessage();

        player.stopTrack();
        player.destroy();

        if (audioChannel != null) {
            audioChannel.getGuild().getAudioManager().closeAudioConnection();
        }

        cleanPlaylist();
    }

    public void shuffleQueue() {
        List<AudioInfo> tempQueue = new ArrayList<>(this.getPlaylist());

        AudioInfo current = tempQueue.get(0);
        tempQueue.remove(0);

        Collections.shuffle(tempQueue);
        tempQueue.add(0, current);

        cleanPlaylist();
        playlist.addAll(tempQueue);
    }

    public void sendPlayingMessage(AudioTrack track, InteractionHook hook) {
        MessageEmbed musicInfo = MusicUtil.buildTrackInfo(track, this).build();

        /*
        Button pauseButton = Button.success("pause", Emoji.fromUnicode("⏸"));
        Button skipButton = Button.primary("skip", Emoji.fromUnicode("⏭"));
        Button shuffleButton = Button.primary("shuffle", Emoji.fromUnicode("🔀"));
        Button repeatButton = Button.danger("repeat", Emoji.fromUnicode("🔁"));

        if (player.isPaused()) {
            pauseButton = Button.danger("pause", Emoji.fromUnicode("▶"));
        }

        if (getTrackInfo() != null && getTrackInfo().isRepeat()) {
            repeatButton = Button.success("repeat", Emoji.fromUnicode("🔂"));
        }*/

        if (hook == null) {
            if (audioChannel == null || textChannelId == 0) return;

            deleteLastMessage();

            Guild guild = audioChannel.getGuild();
            TextChannel textChannel = guild.getTextChannelById(textChannelId);
            if (textChannel == null) {
                textChannelId = 0;
                return;
            }

            textChannel.sendMessageEmbeds(musicInfo)
                    .queue(messsage -> lastMusicMessageId = messsage.getIdLong());
        } else {
            hook.sendMessageEmbeds(musicInfo)
                    .queue(message -> lastMusicMessageId = message.getIdLong());
        }
    }

    public void updatePlayingMessage() {
        if (audioChannel == null) return;

        TextChannel textChannel = audioChannel.getGuild().getTextChannelById(textChannelId);
        if (textChannel == null) {
            textChannelId = 0;
            return;
        }

        MessageEmbed musicInfo = MusicUtil.buildTrackInfo(player.getPlayingTrack(), this).build();
        textChannel.editMessageEmbedsById(lastMusicMessageId, musicInfo).queue();
    }

    @Nullable
    public AudioInfo getTrackInfo() {
        return playlist.stream()
                .filter(audioInfo -> audioInfo.getTrack().equals(player.getPlayingTrack()))
                .findFirst()
                .orElse(null);
    }

    public boolean isPlaying() {
        return player.getPlayingTrack() != null;
    }

    public boolean isPaused() {
        return player.isPaused();
    }

    public Set<AudioInfo> getPlaylist() {
        return new LinkedHashSet<>(playlist);
    }

    public void cleanPlaylist() {
        playlist.clear();
    }

    public void skipTrack() {
        player.stopTrack();
    }

    public FutureBuilder<File> downloadAudio() {
        if (audioChannel == null) return null;

        Guild guild = audioChannel.getGuild();
        AudioManager audio = guild.getAudioManager();

        AudioBridge receivingHandler = (AudioBridge) audio.getReceivingHandler();
        if (receivingHandler == null) return null;

        File tempFile = new File("./temp/", "temp.mp3");
        if (tempFile.exists()) tempFile.delete();

        tempFile.getParentFile().mkdirs();

        return FutureBuilder.of(() -> {
            try {
                List<byte[]> receivedBytes = new ArrayList<>(receivingHandler.receivedAudioDataQueue);
                receivingHandler.clearReceivedData();

                int size = 0;
                for (byte[] bs : receivedBytes) {
                    size += bs.length;
                }

                byte[] decodedData = new byte[size];

                int i = 0;
                for (byte[] bs : receivedBytes) {
                    for (byte b : bs) {
                        decodedData[i++] = b;
                    }
                }

                getWavFile(tempFile, decodedData);
                return tempFile;
            } catch (Exception exception) {
                LoggerUtil.printException(exception);
                return null;
            }
        });
    }

    private void getWavFile(File outFile, byte[] decodedData) throws IOException {
        AudioFormat outputFormat = new AudioFormat(48000.0f, 16, 2, true, true);
        AudioSystem.write(new AudioInputStream(new ByteArrayInputStream(decodedData), outputFormat, decodedData.length), AudioFileFormat.Type.WAVE, outFile);
    }

    public void sendPlayingMessage() {
        AudioTrack track = player.getPlayingTrack();
        if (track == null) return;

        sendPlayingMessage(track, null);
    }
}
