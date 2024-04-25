package com.yuhtin.lauren.module.impl.music;

import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.yuhtin.lauren.Lauren;
import com.yuhtin.lauren.Startup;
import com.yuhtin.lauren.module.Module;
import com.yuhtin.lauren.module.impl.player.module.PlayerModule;
import com.yuhtin.lauren.util.*;
import lombok.val;
import net.dv8tion.jda.api.audio.SpeakingMode;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class MusicModule implements Module {


    private static final Paginator.Builder BUILDER = new Paginator.Builder()
            .setColumns(1)
            .setFinalAction(message -> message.clearReactions().queue())
            .setItemsPerPage(10)
            .setEventWaiter(Startup.getLauren().getEventWaiter())
            .useNumberedItems(true)
            .showPageNumbers(true)
            .wrapPageEnds(true)
            .setTimeout(1, TimeUnit.MINUTES);

    private final HashMap<Long, GuildedMusicPlayer> playerByGuild = new HashMap<>();

    private AudioPlayerManager audioManager;
    private PlayerModule playerModule;

    @Override
    public boolean setup(Lauren lauren) {
        TaskHelper.runTaskLater(() -> playerModule = Module.instance(PlayerModule.class), 3, TimeUnit.SECONDS);

        this.audioManager = new DefaultAudioPlayerManager();
        audioManager.setItemLoaderThreadPoolSize(128);
        audioManager.getConfiguration().setResamplingQuality(AudioConfiguration.ResamplingQuality.HIGH);

        AudioSourceManagers.registerRemoteSources(audioManager);
        AudioSourceManagers.registerLocalSource(audioManager);

        lauren.getJda().addEventListener(this);

        TaskHelper.runTaskTimerAsync(
                () -> playerByGuild.values().forEach(GuildedMusicPlayer::sendPlayingMessage),
                5, 2, TimeUnit.SECONDS
        );

        return true;
    }

    @SubscribeEvent
    public void onButtonInteract(ButtonInteractionEvent event) {
        if (event.getGuild() == null || event.getMember() == null) return;

        event.deferReply().queue(hook -> {
            getByGuildId(event.getGuild()).queue(player -> {
                if (player.getAudioChannel() == null) return;

                if (event.getComponentId().equals("skip")) {
                    skipMannually(event.getGuild(), event.getMember(), hook);
                }

                if (event.getComponentId().equals("repeat")) {
                    repeatMannually(event.getGuild(), event.getMember(), hook);
                }

                if (event.getComponentId().equals("pause")) {
                    pauseMannually(event.getGuild(), event.getMember(), hook);
                }

                if (event.getComponentId().equals("shuffle")) {
                    shuffleMannually(event.getGuild(), event.getMember(), hook);
                }
            });
        });
    }

    @SubscribeEvent
    public void onMessageEvent(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        boolean canUse = true;

        if (!event.getMessage().getEmbeds().isEmpty()) {
            MessageEmbed messageEmbed = event.getMessage().getEmbeds().get(0);
            if (messageEmbed.getTitle() != null && messageEmbed.getTitle().contains("música atual")) {
                canUse = false;
            }
        }

        if (!canUse) return;

        TaskHelper.runTaskLater(() -> {
            getByGuildId(event.getGuild()).queue(trackManager -> {
                trackManager.deleteLastMessage();
                trackManager.sendPlayingMessage();
            });
        }, 1, TimeUnit.SECONDS);
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

                AudioBridge audioBridge = new AudioBridge(guildedMusicPlayer.getPlayer());
                discordAudio.setSendingHandler(audioBridge);
                discordAudio.setReceivingHandler(audioBridge);
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

    public void skipMannually(Guild guild, Member member, InteractionHook hook) {
        if (!MusicUtil.isInVoiceChannel(member)) {
            hook.sendMessage("\uD83C\uDFB6 Amiguinho, entre em algum canal de voz para poder usar comandos de música.").queue();
            return;
        }

        getByGuildId(guild).queue(trackManager -> {
            if (MusicUtil.isIdle(trackManager, hook)) return;

            if (MusicUtil.isMusicOwner(member, trackManager)) {
                trackManager.getPlayer().stopTrack();
                hook.sendMessage("\u23e9 Pulei a música pra você <3").queue();
                return;
            }

            AudioInfo info = trackManager.getTrackInfo();
            if (info.hasVoted(member.getUser())) {
                hook.sendMessage("\uD83D\uDC6E\uD83C\uDFFD\u200D♀️ Ei você já votou pra pular essa música ;-;")
                        .setEphemeral(true)
                        .queue();
                return;
            }

            info.addSkip(member.getUser());
            if (info.getSkips() >= trackManager.getAudioChannel().getMembers().size() - 2) {
                trackManager.skipTrack();
                hook.sendMessage("\uD83E\uDDF6 Amo quando todos concordam entre si, pulando a música").queue();
                return;
            }

            String name = member.getNickname() == null
                    ? member.getUser().getName()
                    : member.getNickname();

            String message = "\uD83E\uDDEC **"
                    + name +
                    "** votou para pular a música **("
                    + info.getSkips() + "/" + (trackManager.getAudioChannel().getMembers().size() - 2)
                    + ")**";

            hook.sendMessage(message).queue();
        });
    }

    public void repeatMannually(Guild guild, Member member, InteractionHook hook) {
        getByGuildId(guild).queue(trackManager -> {
            if (MusicUtil.isIdle(trackManager, hook)) return;
            if (!playerModule.isDJ(member)) {
                hook.sendMessage("Você não é DJ para parar o batidão \uD83D\uDE14").setEphemeral(true).queue();
                return;
            }

            AudioInfo audioInfo = trackManager.getTrackInfo();
            if (audioInfo == null) return;

            audioInfo.setRepeat(!audioInfo.isRepeat());

            val message = audioInfo.isRepeat()
                    ? "<:felizpakas:742373250037710918> Parece que gosta dessa música né, vou tocar ela denovo quando acabar"
                    : "<a:tchau:751941650728747140> Deixa pra lá, vou repetir a música mais não";

            hook.sendMessage(message).queue();

            trackManager.sendPlayingMessage();
        });
    }

    public void pauseMannually(Guild guild, Member member, InteractionHook hook) {
        getByGuildId(guild).queue(trackManager -> {
            if (MusicUtil.isIdle(trackManager, hook)) return;
            if (!playerModule.isDJ(member)) {
                hook.sendMessage("Você não é DJ para parar o batidão \uD83D\uDE14").setEphemeral(true).queue();
                return;
            }

            trackManager.getPlayer().setPaused(!trackManager.getPlayer().isPaused());

            val message = trackManager.getPlayer().isPaused() ?
                    "\uD83E\uDD7A Taxaram meu batidão, espero que me liberem logo"
                    : "\uD83E\uDD73 Liberaram meu batidão uhhuuuu";

            hook.sendMessage(message).queue();
            trackManager.sendPlayingMessage();
        });
    }

    public void shuffleMannually(Guild guild, Member member, InteractionHook hook) {
        getByGuildId(guild).queue(trackManager -> {
            if (MusicUtil.isIdle(trackManager, hook)) return;
            if (!playerModule.isDJ(member)) {
                hook.sendMessage("Você não é DJ para parar o batidão \uD83D\uDE14").setEphemeral(true).queue();
                return;
            }

            trackManager.shuffleQueue();
            hook.sendMessage("<a:infinito:703187274912759899> Misturando a lista de músicas").queue();

            sendQueue(1, guild, member, null, hook.getInteraction().getMessageChannel());
        });
    }

    public void sendQueue(int page, Guild guild, Member member, InteractionHook hook, MessageChannel channel) {
        getByGuildId(guild).queue(trackManager -> {
            if (MusicUtil.isIdle(trackManager, hook)) return;
            if (trackManager.getPlaylist().isEmpty()) {
                MessageEmbed embed = EmbedUtil.create("Eita não tem nenhum batidão tocando, adiciona uns ai <3");

                if (hook == null) {
                    channel.sendMessageEmbeds(embed).queue();
                } else {
                    hook.sendMessageEmbeds(embed).queue();
                }

                return;
            }

            val queue = trackManager.getPlaylist();
            val songs = new String[queue.size()];
            var totalTime = 0L;

            var i = 0;
            for (val audioInfo : queue) {
                totalTime += audioInfo.getTrack().getInfo().length;
                songs[i] = audioInfo.toString();

                ++i;
            }

            val timeInLetter = MusicUtil.getTimeStamp(totalTime);
            BUILDER.setText((number, number2) -> {
                        val stringBuilder = new StringBuilder();
                        if (trackManager.getPlayer().getPlayingTrack() != null) {
                            stringBuilder.append(trackManager.getPlayer().isPaused() ? "\u23F8" : "\u25B6")
                                    .append(" **")
                                    .append(trackManager.getPlayer().getPlayingTrack().getInfo().title)
                                    .append("**")
                                    .append(" - ")
                                    .append("`")
                                    .append(MusicUtil.getTimeStamp(trackManager.getPlayer().getPlayingTrack().getPosition()))
                                    .append(" / ")
                                    .append(MusicUtil.getTimeStamp(trackManager.getPlayer().getPlayingTrack().getInfo().length))
                                    .append("`")
                                    .append("\n");
                        }

                        return stringBuilder.append("\uD83D\uDCBF Informações da Fila | ")
                                .append(queue.size())
                                .append(" músicas | `")
                                .append(timeInLetter)
                                .append("`")
                                .toString();
                    })
                    .setItems(songs)
                    .setUsers(member.getUser())
                    .setColor(member.getColor());

            if (hook == null) {
                BUILDER.build().paginate(channel, page);
            } else {
                BUILDER.build().paginate(hook, page);
            }
        });
    }
}
