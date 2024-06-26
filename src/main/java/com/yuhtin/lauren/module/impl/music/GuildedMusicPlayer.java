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
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
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

        if (playlist.isEmpty()) {
            deleteLastMessage();
            return;
        }

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
        deleteLastMessage();

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

    public void sendPlayingMessage(AudioTrack track, InteractionHook hook) {
        MessageEmbed musicInfo = MusicUtil.showTrackInfo(track, this).build();

        Button pauseButton = Button.success("pause", Emoji.fromUnicode("⏸"));
        Button skipButton = Button.primary("skip", Emoji.fromUnicode("⏭"));
        Button shuffleButton = Button.primary("shuffle", Emoji.fromUnicode("🔀"));
        Button repeatButton = Button.danger("repeat", Emoji.fromUnicode("🔁"));

        if (player.isPaused()) {
            pauseButton = Button.danger("pause", Emoji.fromUnicode("▶"));
        }

        if (getTrackInfo() != null && getTrackInfo().isRepeat()) {
            repeatButton = Button.success("repeat", Emoji.fromUnicode("🔂"));
        }

        if (hook == null) {
            Guild guild = audioChannel.getGuild();
            TextChannel textChannel = guild.getTextChannelById(textChannelId);
            if (textChannel == null) {
                textChannelId = 0;
                return;
            }

            if (lastMusicMessageId != 0) {
                MessageEditData data = MessageEditData.fromEmbeds(musicInfo);

                Button finalPauseButton = pauseButton;
                Button finalRepeatButton = repeatButton;

                textChannel.editMessageById(lastMusicMessageId, data)
                        .setActionRow(pauseButton, skipButton, shuffleButton, repeatButton)
                        .queue(s -> {}, m -> {
                            textChannel.sendMessageEmbeds(musicInfo)
                                    .addActionRow(finalPauseButton, skipButton, shuffleButton, finalRepeatButton)
                                    .queue(messsage -> setLastMusicMessageId(messsage.getIdLong()));
                        });
            } else {
                textChannel.sendMessageEmbeds(musicInfo)
                        .addActionRow(pauseButton, skipButton, shuffleButton, repeatButton)
                        .queue(messsage -> setLastMusicMessageId(messsage.getIdLong()));
            }
        } else {
            deleteLastMessage();

            hook.sendMessageEmbeds(musicInfo)
                    .addActionRow(pauseButton, skipButton, shuffleButton, repeatButton)
                    .queue(messsage -> {
                        setTextChannelId(messsage.getChannelIdLong());
                        setLastMusicMessageId(messsage.getIdLong());
                    });
        }
    }

    public void sendPlayingMessage() {
        AudioTrack track = player.getPlayingTrack();
        if (track == null) return;

        sendPlayingMessage(track, null);
    }
}
