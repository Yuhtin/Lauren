package music;

import application.Lauren;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.requests.data.tracks.GetTrackRequest;
import logger.Logger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import utils.helper.Utilities;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class TrackManager extends AudioEventAdapter {
    private final GuildMusicManager musicManager;
    private final AudioPlayerManager audioManager;
    private final SpotifyApi spotifyApi;
    public final AudioPlayer player;

    public TrackManager() {
        this.audioManager = new DefaultAudioPlayerManager();
        this.player = audioManager.createPlayer();
        spotifyApi = new SpotifyApi.Builder().setAccessToken(Lauren.config.spotifyClient).build();

        musicManager = new GuildMusicManager(player);
        AudioSourceManagers.registerRemoteSources(audioManager);
        AudioSourceManagers.registerLocalSource(audioManager);
    }

    public void loadTrack(String trackUrl, Member member, Message message, TextChannel channel) {
        if (member.getVoiceState() == null || member.getVoiceState().getChannel() == null) {
            channel.sendMessage("\ud83d\udcbf Você não está em um canal de voz \uD83D\uDE2D").queue();
            return;
        }
        message.getTextChannel().sendTyping().queue();

        if (trackUrl.contains("spotify.com")) {
            String[] parsed = trackUrl.split("/track/");
            if (parsed.length == 2) {
                GetTrackRequest build = spotifyApi.getTrack(parsed[1]).build();
                try {
                    trackUrl = build.execute().getName();
                } catch (Exception exception) {
                    exception.printStackTrace();
                    Logger.log("An error occurred on try access Spotify Api").save();
                }
            }
        }

        String finalTrackUrl = trackUrl;
        audioManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {

            @Override
            public void trackLoaded(AudioTrack track) {
                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle("💿 " + Utilities.getFullName(member.getUser()) + " adicionou 1 música a fila")
                        .setDescription(
                                "\ud83d\udcc0 Nome: `" + track.getInfo().title + "`\n" +
                                        "\uD83D\uDCB0 Autor: `" + track.getInfo().author + "`\n" +
                                        "\uD83D\uDCE2 Tipo de vídeo: `" +
                                        (track.getInfo().isStream ? "Stream" : track.getInfo().title.contains("Podcast") ?
                                                "Podcast" : "Música") + "`\n" +
                                        "\uD83D\uDCCC Link: [Clique aqui](" + track.getInfo().uri + ")");

                channel.sendMessage(embed.build()).queue();
                play(track, member);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                if (playlist.getSelectedTrack() != null) {
                    trackLoaded(playlist.getSelectedTrack());
                } else if (playlist.isSearchResult()) {
                    trackLoaded(playlist.getTracks().get(0));
                } else {
                    EmbedBuilder embed = new EmbedBuilder()
                            .setTitle("💿 " + Utilities.getFullName(member.getUser()) + " adicionou " + playlist.getTracks().size() + " músicas a fila")
                            .setDescription("\uD83D\uDCBD Informações da playlist:\n" +
                                    "\ud83d\udcc0 Nome: `" + playlist.getName() + "`\n" +
                                    "\uD83C\uDFB6 Músicas: `" + playlist.getTracks().size() + "`\n\n" +
                                    "\uD83D\uDCCC Link: [Clique aqui](" + finalTrackUrl + ")");

                    channel.sendMessage(embed.build()).queue();
                    for (int i = 0; i < Math.min(playlist.getTracks().size(), 200); ++i) {
                        play(playlist.getTracks().get(i), member);
                    }
                }
            }

            @Override
            public void noMatches() {

                channel.sendMessage("\uD83D\uDC94 Como assim??? Você quer quebrar meus sistemas? \uD83D\uDE2D")
                        .queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
                channel.sendMessage("\uD83D\uDCCC Não consegui encontrar nada relacionado ao que me enviou :p")
                        .queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                exception.printStackTrace();
                channel.sendMessage("\uD83D\uDC94 Como assim??? Você quer quebrar meus sistemas? \uD83D\uDE2D")
                        .queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
                channel.sendMessage("\uD83D\uDCCC Esse formato de arquivo não é valido \uD83D\uDEE9")
                        .queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
            }
        });

        message.delete().queue();
    }

    private void play(AudioTrack track, Member member) {
        musicManager.scheduler.queue(track, member);
    }

    public void shuffleQueue() {
        List<AudioInfo> tQueue = new ArrayList<>(this.getQueuedTracks());

        AudioInfo current = tQueue.get(0);
        tQueue.remove(0);

        Collections.shuffle(tQueue);
        tQueue.add(0, current);

        purgeQueue();
        musicManager.scheduler.queue.addAll(tQueue);
    }

    public Set<AudioInfo> getQueuedTracks() { return new LinkedHashSet<>(musicManager.scheduler.queue); }

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