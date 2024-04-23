package com.yuhtin.lauren.module.impl.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.yuhtin.lauren.util.FutureBuilder;
import com.yuhtin.lauren.util.LoggerUtil;
import com.yuhtin.lauren.util.MusicUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import lombok.val;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
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
        this.player.setVolume(25);

        player.addListener(this);
        player.setFilterFactory(equalizer);
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
        playlist.remove();

        if (playlist.isEmpty()) return;

        // play next track (actual head)
        player.playTrack(playlist.element().getTrack());
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        LoggerUtil.printException(exception);
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
        player.stopTrack();
        player.destroy();

        if (audioChannel != null) {
            audioChannel.getGuild().getAudioManager().closeAudioConnection();
        }

        cleanPlaylist();
    }

    public void play(AudioTrack track, Member member) {
        AudioInfo info = new AudioInfo(track, member);
        playlist.add(info);

        if (this.player.getPlayingTrack() == null) this.player.playTrack(track);
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

    @Nullable
    public AudioInfo getTrackInfo() {
        return playlist.stream()
                .filter(audioInfo -> audioInfo.getTrack().equals(player.getPlayingTrack()))
                .findFirst()
                .orElse(null);
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
        LoggerUtil.getLogger().info("T1");
        if (audioChannel == null) return null;

        LoggerUtil.getLogger().info("T2");

        LoggerUtil.getLogger().info("Downloading audio from " + audioChannel.getGuild().getName());
        Guild guild = audioChannel.getGuild();

        LoggerUtil.getLogger().info("T3");

        File tempFile;
        AudioBridge receivingHandler;
        val audio = guild.getAudioManager();

        LoggerUtil.getLogger().info("T4");
        try {
            LoggerUtil.getLogger().info("T5");
            receivingHandler = (AudioBridge) audio.getReceivingHandler();
            if (receivingHandler == null) {
                LoggerUtil.getLogger().info("T6 null receiving handler");
                return null;
            }

            tempFile = new File("./temp/", "temp.mp3");
            if (tempFile.exists()) tempFile.delete();

            tempFile.getParentFile().mkdirs();

            LoggerUtil.getLogger().info("T7");
        } catch (Exception exception) {
            LoggerUtil.printException(exception);
            return null;
        }

        return FutureBuilder.of(() -> {
            try {
                LoggerUtil.getLogger().info("T8");
                int size = 0;

                List<byte[]> receivedBytes = new ArrayList<>(receivingHandler.receivedAudioDataQueue);

                for (byte[] bs : receivedBytes) {
                    size += bs.length;
                }

                LoggerUtil.getLogger().info("Received " + receivedBytes.size() + " bytes with " + size + " bytes in total");

                byte[] decodedData = new byte[size];
                int i = 0;
                for (byte[] bs : receivedBytes) {
                    for (byte b : bs) {
                        decodedData[i++] = b;
                    }
                }

                LoggerUtil.getLogger().info("T9");
                getWavFile(tempFile, decodedData);
                return tempFile;
            } catch (Exception exception) {
                LoggerUtil.printException(exception);
                return null;
            }
        });
    }

    private void getWavFile(File outFile, byte[] decodedData) throws IOException {
        AudioFormat format = new AudioFormat(8000, 16, 1, true, false);
        AudioSystem.write(new AudioInputStream(new ByteArrayInputStream(decodedData), format, decodedData.length), AudioFileFormat.Type.WAVE, outFile);

        LoggerUtil.getLogger().info("T10");
    }

    public void sendPlayingMessage(AudioTrack track, InteractionHook hook) {
        deleteLastMessage();

        MessageEmbed musicInfo = MusicUtil.showTrackInfo(track, this).build();
        if (hook == null) {
            Guild guild = audioChannel.getGuild();
            TextChannel textChannel = guild.getTextChannelById(textChannelId);
            if (textChannel == null) {
                textChannelId = 0;
                return;
            }

            textChannel.sendMessageEmbeds(musicInfo)
                    .queue(messsage -> setLastMusicMessageId(messsage.getIdLong()));
        } else {
            hook.sendMessageEmbeds(musicInfo).queue(messsage -> {
                setTextChannelId(messsage.getChannelIdLong());
                setLastMusicMessageId(messsage.getIdLong());
            });
        }
    }
}
