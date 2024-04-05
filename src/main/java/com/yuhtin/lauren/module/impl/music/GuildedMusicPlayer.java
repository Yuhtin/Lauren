package com.yuhtin.lauren.module.impl.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.yuhtin.lauren.util.LoggerUtil;
import com.yuhtin.lauren.util.MusicUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
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

public class GuildedMusicPlayer extends AudioEventAdapter {

    // Guild ID - Owner of that player
    private final long guildId;

    // Music data related
    @Getter
    private final AudioPlayer player;
    private final BlockingQueue<AudioInfo> playlist = new LinkedBlockingQueue<>();

    // Channels
    @Nullable private AudioChannel audioChannel;
    @Setter private long lastMusicMessageId;
    @Setter private long textChannelId;


    public GuildedMusicPlayer(long guildId, AudioPlayer player) {
        this.guildId = guildId;
        this.player = player;
        this.player.setVolume(25);

        player.addListener(this);
        player.setFilterFactory(new MusicCustomEqualizer());
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

        if (playlist.isEmpty()) {
            if (audioChannel != null) {
                audioChannel.getGuild().getAudioManager().closeAudioConnection();
                audioChannel = null;
            }
            return;
        }

        // play next track (actual head)
        player.playTrack(playlist.element().getTrack());
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

    public Set<AudioInfo> getPlaylist() {
        return new LinkedHashSet<>(playlist);
    }

    @Nullable
    public AudioInfo getTrackInfo() {
        return playlist.stream()
                .filter(audioInfo -> audioInfo.getTrack().equals(player.getPlayingTrack()))
                .findFirst()
                .orElse(null);
    }

    public void cleanPlaylist() {
        playlist.clear();
    }

    public void skipTrack() {
        player.stopTrack();
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        if (audioChannel == null || textChannelId == 0) return;

        Guild guild = audioChannel.getGuild();
        guild.getAudioManager().openAudioConnection(audioChannel);

        deleteLastMessage();

        TextChannel textChannel = guild.getTextChannelById(textChannelId);
        if (textChannel == null) {
            textChannelId = 0;
            return;
        }

        EmbedBuilder embedBuilder = MusicUtil.showTrackInfo(track, this);
        textChannel.sendMessageEmbeds(embedBuilder.build()).queue(message -> lastMusicMessageId = message.getIdLong());
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

    public File downloadAudio() {
        if (audioChannel == null) return null;

        Guild guild = audioChannel.getGuild();

        val audio = guild.getAudioManager();
        val receivingHandler = (AudioBridge) audio.getReceivingHandler();
        if (receivingHandler == null) return null;

        val tempFile = new File("./temp/", "temp.mp3");
        if (tempFile.exists()) tempFile.delete();

        tempFile.getParentFile().mkdirs();

        try {
            int size = 0;

            List<byte[]> rescievedBytes = new ArrayList<>(receivingHandler.receivedAudioDataQueue);
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
        } catch (Exception exception) {
            LoggerUtil.printException(exception);
            return null;
        }
    }

    private void getWavFile(File outFile, byte[] decodedData) throws IOException {
        AudioFormat format = new AudioFormat(8000, 16, 1, true, false);
        AudioSystem.write(new AudioInputStream(new ByteArrayInputStream(decodedData), format, decodedData.length), AudioFileFormat.Type.WAVE, outFile);
    }

}
