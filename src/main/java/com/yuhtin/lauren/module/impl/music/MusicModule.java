package com.yuhtin.lauren.module.impl.music;

import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.yuhtin.lauren.Lauren;
import com.yuhtin.lauren.module.Module;
import com.yuhtin.lauren.util.FutureBuilder;
import com.yuhtin.lauren.util.LoggerUtil;
import lombok.val;
import net.dv8tion.jda.api.audio.SpeakingMode;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class MusicModule implements Module {

    private final HashMap<Long, GuildedMusicPlayer> playerByGuild = new HashMap<>();

    private AudioPlayerManager audioManager;

    @Override
    public boolean setup(Lauren lauren) {
        this.audioManager = new DefaultAudioPlayerManager();
        audioManager.setItemLoaderThreadPoolSize(128);
        audioManager.getConfiguration().setResamplingQuality(AudioConfiguration.ResamplingQuality.HIGH);

        AudioSourceManagers.registerRemoteSources(audioManager);
        AudioSourceManagers.registerLocalSource(audioManager);

        return true;
    }

    public int getMusicsInQueue() {
        return playerByGuild.values()
                .stream()
                .mapToInt(player -> player.getPlaylist().size())
                .sum();
    }

    public FutureBuilder<GuildedMusicPlayer> getByGuildId(Guild guild) {
        return FutureBuilder.of(() -> {
            try {
                long guildId = guild.getIdLong();
                if (playerByGuild.containsKey(guildId)) {
                    return playerByGuild.get(guildId);
                }

                GuildedMusicPlayer guildedMusicPlayer = new GuildedMusicPlayer(guildId, audioManager.createPlayer());

                AudioManager discordAudio = guild.getAudioManager();

                discordAudio.setSendingHandler(new AudioBridge(guildedMusicPlayer.getPlayer()));
                discordAudio.setSpeakingMode(SpeakingMode.PRIORITY);

                playerByGuild.put(guildId, guildedMusicPlayer);
                return guildedMusicPlayer;
            } catch (Exception e) {
                LoggerUtil.getLogger().severe("Error while getting music player by guild id");
                LoggerUtil.printException(e);
                return null;
            }
        });
    }

    public void loadTrack(String trackUrl, Member member, @Nullable InteractionHook hook, MusicSearchType type) {
        LoggerUtil.getLogger().info("Loading track " + trackUrl + " [" + type + "] requested by " + member.getUser().getName());

        getByGuildId(member.getGuild()).queue(player -> {
            String trackEmoji = trackUrl.contains("spotify.com") ? "<:spotify:751049445592006707>" : "<:youtube:751031330057486366>";
            val handlerBuilder = MusicSearchEngine.builder()
                    .player(player)
                    .trackUrl(trackUrl)
                    .member(member)
                    .searchType(type);

            if (hook != null) {
                player.setTextChannelId(hook.getInteraction().getChannelIdLong());
            }

            if (type == MusicSearchType.SIMPLE_SEARCH && hook != null) {
                hook.sendMessage(trackEmoji + " **Procurando** 🔎 `" + trackUrl.replace("ytsearch: ", "") + "`").queue(message -> {
                    handlerBuilder.message(message);
                    audioManager.loadItemOrdered(player, trackUrl, handlerBuilder.build());
                });
                return;
            }

            audioManager.loadItemOrdered(player, trackUrl, handlerBuilder.build());
        });
    }

    public void destroy(long guildId) {
        GuildedMusicPlayer guildedMusicPlayer = playerByGuild.remove(guildId);
        guildedMusicPlayer.destroy();
    }

}
