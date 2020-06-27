package com.yuhtin.lauren.core.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.yuhtin.lauren.core.logger.LogType;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.utils.helper.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class TrackManager extends AudioEventAdapter {
    public final GuildMusicManager musicManager;
    public final AudioPlayerManager audioManager;
    public final AudioPlayer player;

    public TrackManager() {
        this.audioManager = new DefaultAudioPlayerManager();
        this.player = audioManager.createPlayer();

        musicManager = new GuildMusicManager(player);
        AudioSourceManagers.registerRemoteSources(audioManager);
        AudioSourceManagers.registerLocalSource(audioManager);
    }

    public void loadTrack(String trackUrl, Member member, TextChannel channel) {
        channel.sendTyping().queue();
        audioManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {

            @Override
            public void trackLoaded(AudioTrack track) {
                if (player.isPaused()) player.setPaused(false);

                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle("💿 " + Utilities.getFullName(member.getUser()) + " adicionou 1 música a fila")
                        .setDescription(
                                "\ud83d\udcc0 Nome: `" + track.getInfo().title + "`\n" +
                                        "\uD83D\uDCB0 Autor: `" + track.getInfo().author + "`\n" +
                                        "\uD83D\uDCE2 Tipo de vídeo: `" +
                                        (track.getInfo().isStream ? "Stream" : track.getInfo().title.contains("Podcast") ?
                                                "Podcast" : "Música") + "`\n" +
                                        "\uD83D\uDCCC Link: [Clique aqui](" + track.getInfo().uri + ")");

                Logger.log("The player " + Utilities.getFullName(member.getUser()) + " added a music", LogType.LOG).save();
                play(track, member);
                channel.sendMessage(embed.build()).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                if (playlist.getSelectedTrack() != null) {
                    trackLoaded(playlist.getSelectedTrack());
                } else if (playlist.isSearchResult()) {
                    trackLoaded(playlist.getTracks().get(0));
                } else {
                    if (player.isPaused()) player.setPaused(false);

                    int limit = Utilities.isBooster(member) || Utilities.isDJ(member, null, false) ? 100 : 25;
                    int maxMusics = Math.min(playlist.getTracks().size(), limit);

                    EmbedBuilder embed = new EmbedBuilder()
                            .setTitle("💿 " + Utilities.getFullName(member.getUser()) + " adicionou " + maxMusics + " músicas a fila")
                            .setDescription("\uD83D\uDCBD Informações da playlist:\n" +
                                    "\ud83d\udcc0 Nome: `" + playlist.getName() + "`\n" +
                                    "\uD83C\uDFB6 Músicas: `" + maxMusics + "`\n\n" +
                                    "\uD83D\uDCCC Link: [Clique aqui](" + trackUrl + ")");

                    Logger.log("The player " + Utilities.getFullName(member.getUser()) + " added a playlist with " + maxMusics + " musics", LogType.LOG).save();
                    for (int i = 0; i < maxMusics; ++i) {
                        play(playlist.getTracks().get(i), member);
                    }

                    channel.sendMessage(embed.build()).queue();
                }
            }

            @Override
            public void noMatches() {
                channel.sendMessage("\uD83D\uDC94 Como assim??? Você quer quebrar meus sistemas? \uD83D\uDE2D")
                        .queue(message -> message.delete().queueAfter(5, TimeUnit.SECONDS));
                channel.sendMessage("\uD83D\uDCCC Não consegui encontrar nada relacionado ao que me enviou :p")
                        .queue(message -> message.delete().queueAfter(5, TimeUnit.SECONDS));
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                channel.sendMessage("\uD83D\uDC94 Como assim??? Você quer quebrar meus sistemas? \uD83D\uDE2D")
                        .queue(message -> message.delete().queueAfter(5, TimeUnit.SECONDS));
                channel.sendMessage("\uD83D\uDCCC Este link não é suportado ou a playlist é privada \uD83D\uDEE9")
                        .queue(message -> message.delete().queueAfter(5, TimeUnit.SECONDS));
            }
        });
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

    public void remove(AudioInfo entry) {
        musicManager.scheduler.queue.remove(entry);
    }

    public AudioInfo getTrackInfo(AudioTrack track) {
        return musicManager.scheduler.queue.stream().filter(audioInfo -> audioInfo.getTrack().equals(track)).findFirst().orElse(null);
    }
}