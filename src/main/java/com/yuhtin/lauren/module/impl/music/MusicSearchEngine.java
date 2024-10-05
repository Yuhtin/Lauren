package com.yuhtin.lauren.module.impl.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.yuhtin.lauren.module.Module;
import com.yuhtin.lauren.module.impl.player.module.PlayerModule;
import com.yuhtin.lauren.util.EmbedUtil;
import com.yuhtin.lauren.util.LoggerUtil;
import com.yuhtin.lauren.util.MusicUtil;
import com.yuhtin.lauren.util.TaskHelper;
import lombok.Builder;
import lombok.Data;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.util.function.Consumer;

@Builder
@Data
public class MusicSearchEngine implements AudioLoadResultHandler {

    private final GuildedMusicPlayer player;
    private final String trackUrl;
    private final Member member;
    private final InteractionHook hook;
    private final MusicSearchType searchType;
    private final Consumer<AudioTrack> trackFoundConsumer;

    @Override
    public void trackLoaded(AudioTrack track) {
        LoggerUtil.getLogger().info("The player " + member.getUser().getName() + " added a music to queue");
        if (searchType == MusicSearchType.ADDING_TO_PLAYLIST) {
            LoggerUtil.getLogger().info("The player " + member.getUser().getName() + " added a music to playlist");
            trackFoundConsumer.accept(track);
            return;
        }

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(EmbedUtil.getColor())
                .setFooter("Requested by " + member.getUser().getName())
                .setDescription(
                        "### \ud83d\udcc0 " + member.getUser().getName() + " adicionou `" + track.getInfo().title + "`\n" +
                                "👤 Autor: `" + track.getInfo().author + "`\n" +
                                "\uD83D\uDCCC Link: [Click here](" + track.getInfo().uri + ")"
                );

        if (hook != null) {
            hook.editOriginal("📀 Você adicionou `" + track.getInfo().title + "` à fila! [" + MusicUtil.getTimeStamp(track.getDuration()) + "]")
                    .setComponents()
                    .setEmbeds()
                    .queue();

            hook.retrieveOriginal().queue(message -> {
                message.getChannel().sendMessageEmbeds(embed.build()).queue();
            });
        }

        player.setAudioChannel(member.getVoiceState().getChannel());
        player.play(track, member);
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        LoggerUtil.getLogger().info("The player " + member.getUser().getName() + " added a playlist with " + playlist.getTracks().size() + " musics");
        if (searchType == MusicSearchType.ADDING_TO_PLAYLIST) {
            AudioTrack selectedTrack = playlist.getSelectedTrack() != null ? playlist.getSelectedTrack() : playlist.getTracks().get(0);
            trackFoundConsumer.accept(selectedTrack);

            return;
        }

        LoggerUtil.getLogger().info("The player " + member.getUser().getName() + " added a playlist with " + playlist.getTracks().size() + " musics");
        if (hook != null && playlist.getTracks().size() > 1 && playlist.isSearchResult()) {
            MusicModule musicModule = Module.instance(MusicModule.class);
            if (musicModule != null) {
                musicModule.sendSearchResult(trackUrl, playlist.getTracks(), hook);
                return;
            }
        }

        if (playlist.getSelectedTrack() != null) trackLoaded(playlist.getSelectedTrack());
        else if (playlist.isSearchResult()) trackLoaded(playlist.getTracks().get(0));
        else {
            int maxMusics = Math.min(playlist.getTracks().size(), 150);

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("💿 " + member.getUser().getName() + " adicionou " + maxMusics + " músicas a fila")
                    .setDescription("\uD83D\uDCBD Informações da playlist:\n\n" +
                            "\ud83d\udcc0 Nome: `" + playlist.getName() + "`\n" +
                            "\uD83C\uDFB6 Músicas: `" + maxMusics + "`\n" +
                            "\uD83D\uDCCC Link: [Clique aqui](" + trackUrl + ")");

            LoggerUtil.getLogger().info("The player " + member.getUser().getName() + " added a playlist with " + maxMusics + " musics");

            player.setAudioChannel(member.getVoiceState().getChannel());

            TaskHelper.runAsync(() -> {
                for (int i = 0; i < maxMusics; i++) {
                    val track = playlist.getTracks().get(i);

                    if (track.getInfo().title != null) {
                        player.play(track, member);
                    } else {
                        val link = "https://youtube.com/watch?v=" + track.getIdentifier();

                        MusicModule musicModule = Module.instance(MusicModule.class);
                        if (musicModule != null) {
                            musicModule.loadTrack(MusicSearchType.LOOKING_PLAYLIST, link, member, null, null);
                        }
                    }
                }
            });

            hook.sendMessageEmbeds(embed.build()).queue();
        }
    }

    @Override
    public void noMatches() {
        if (hook == null) return;
        hook.sendMessage("\uD83D\uDCCC `I can't find this video or playlist`").queue();
    }

    @Override
    public void loadFailed(FriendlyException exception) {
        LoggerUtil.printException(exception);

        if (hook == null) return;
        hook.sendMessage("\uD83D\uDCCC `The track failed to load, maybe it's private?`").queue();
    }

}
