package com.yuhtin.lauren.module.impl.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.audio.CombinedAudio;
import net.dv8tion.jda.api.audio.UserAudio;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AudioBridge implements AudioReceiveHandler, AudioSendHandler {

    double volume = 1.0;
    ConcurrentLinkedQueue<byte[]> receivedAudioDataQueue = new ConcurrentLinkedQueue<>();
    private final AudioPlayer audioPlayer;
    private AudioFrame lastFrame;


    public AudioBridge(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
    }

    @Override
    public boolean canReceiveCombined() {
        return true;
    }

    @Override
    public boolean canReceiveUser() {
        return false;
    }

    @Override
    public void handleCombinedAudio(CombinedAudio combinedAudio) {
        receivedAudioDataQueue.add(combinedAudio.getAudioData(volume));
    }

    @Override
    public void handleUserAudio(@NotNull UserAudio userAudio) {
    }

    @Override
    public boolean canProvide() {
        lastFrame = audioPlayer.provide();
        return lastFrame != null;
    }

    @Override
    public ByteBuffer provide20MsAudio() {
        return ByteBuffer.wrap(lastFrame.getData());
    }

    @Override
    public boolean isOpus() {
        return true;
    }

    public void clearReceivedData() {
        receivedAudioDataQueue.clear();
    }
}