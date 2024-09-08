package com.yuhtin.lauren.module.impl.music;

import com.github.topisenpai.lavasrc.spotify.SpotifySourceManager;
import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.yuhtin.lauren.Lauren;
import com.yuhtin.lauren.Startup;
import com.yuhtin.lauren.database.MongoModule;
import com.yuhtin.lauren.database.MongoOperation;
import com.yuhtin.lauren.database.OperationFilter;
import com.yuhtin.lauren.module.Module;
import com.yuhtin.lauren.module.impl.music.customplaylist.CustomPlaylist;
import com.yuhtin.lauren.module.impl.music.customplaylist.PlaylistTrackInfo;
import com.yuhtin.lauren.module.impl.player.module.PlayerModule;
import com.yuhtin.lauren.util.*;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import dev.lavalink.youtube.clients.*;
import lombok.NonNull;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.audio.SpeakingMode;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    private static final String[] EMOJIS = new String[]{"1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣", "6️⃣", "7️⃣", "8️⃣", "9️⃣", "🔟"};

    private final HashMap<Long, GuildedMusicPlayer> playerByGuild = new HashMap<>();
    private final HashMap<Long, CustomPlaylist> customPlaylistByGuildId = new HashMap<>();
    private final HashMap<String, List<AudioTrack>> searchCache = new HashMap<>();

    private AudioPlayerManager audioManager;
    private PlayerModule playerModule;

    @Override
    public boolean setup(Lauren lauren) {
        TaskHelper.runTaskLater(() -> playerModule = Module.instance(PlayerModule.class), 3, TimeUnit.SECONDS);

        this.audioManager = new DefaultAudioPlayerManager();
        audioManager.setItemLoaderThreadPoolSize(128);
        audioManager.getConfiguration().setResamplingQuality(AudioConfiguration.ResamplingQuality.HIGH);

        // dev.lavalink.youtube.http.YoutubeOauth2Handler log to info
        Logger.getLogger("dev.lavalink.youtube.http.YoutubeOauth2Handler").setLevel(Level.INFO);

        YoutubeAudioSourceManager ytSourceManager = new YoutubeAudioSourceManager(true, new Music(), new Web(), new TvHtml5Embedded(), new WebEmbedded(), new AndroidMusic(), new Android());
        ytSourceManager.useOauth2(null, false);

        audioManager.registerSourceManager(ytSourceManager);
        audioManager.registerSourceManager(new SpotifySourceManager(new String[]{"ytmsearch:\"%ISRC%\"", "ytmsearch:%QUERY%"}, "655bbcedce534fb2b85f236f879c3007", "541f921a61cd4f098caa3f99adba23b3", null, audioManager));

        AudioSourceManagers.registerLocalSource(audioManager);

        lauren.getJda().addEventListener(this);

        MongoModule mongoModule = Module.instance(MongoModule.class);
        mongoModule.registerBinding("customplaylists", CustomPlaylist.class);

        MongoOperation.bind(CustomPlaylist.class)
                .filter(OperationFilter.NOT_EQUALS, "guildId", -1)
                .findMany()
                .queue(data -> {
                    for (CustomPlaylist playlist : data) {
                        customPlaylistByGuildId.put(playlist.getPrimaryKey(), playlist);
                    }
                });

        /*TaskHelper.runTaskTimerAsync(
                () -> playerByGuild.values().forEach(GuildedMusicPlayer::sendPlayingMessage),
                5, 1, TimeUnit.SECONDS
        );*/

        TaskHelper.runTaskTimer(() -> {
            for (GuildedMusicPlayer musicPlayer : playerByGuild.values()) {
                if (musicPlayer.getLastMusicMessageId() == 0) continue;
                if (musicPlayer.getAudioChannel() == null) continue;
                if (!musicPlayer.isPlaying() || musicPlayer.isPaused()) continue;

                musicPlayer.updatePlayingMessage();
            }
        }, 2, 2, TimeUnit.SECONDS);

        lauren.getGuild().upsertCommand("customplaylist", "Configure custom playlist")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.VOICE_MUTE_OTHERS))
                .addSubcommands(
                        new SubcommandData("list", "List songs in the custom playlist"),
                        new SubcommandData("add", "Add a song to the custom playlist")
                                .addOption(OptionType.STRING, "music", "Link or name of the music", true),
                        new SubcommandData("remove", "Remove a song from the custom playlist")
                                .addOption(OptionType.INTEGER, "index", "Index of the song", true),
                        new SubcommandData("clear", "Clear the custom playlist"),
                        new SubcommandData("autoplay", "Enable/Disable autoplay of the custom playlist"),
                        new SubcommandData("shuffle", "Shuffle the custom playlist")
                ).queue();

        return true;
    }

    @SubscribeEvent
    public void onPlaylistCommand(SlashCommandInteractionEvent event) {
        if (event.getMember() == null || event.getGuild() == null) return;
        if (!event.getName().equalsIgnoreCase("customplaylist")) return;

        event.deferReply(true).queue(hook -> {
            CustomPlaylist playlist = getCustomPlaylist(event.getGuild().getIdLong());
            String subcommandName = event.getSubcommandName();
            if (subcommandName == null) {
                hook.sendMessage("No subcommand specified").queue();
                return;
            }

            if (subcommandName.equalsIgnoreCase("list")) {
                StringBuilder sb = new StringBuilder();

                int index = 1;
                for (PlaylistTrackInfo track : playlist.getTracks()) {
                    sb.append(index).append(". ").append(track).append("\n");
                    index++;
                }

                if (!sb.isEmpty()) {
                    sb.setLength(sb.length() - 1);
                } else {
                    sb.append("No songs in the playlist");
                }

                hook.sendMessageEmbeds(EmbedUtil.createDefaultEmbed(sb.toString()).build()).queue();
                return;
            }

            if (subcommandName.equalsIgnoreCase("add")) {
                OptionMapping musicOption = event.getOption("music");
                if (musicOption == null) {
                    hook.sendMessage("No music specified").queue();
                    return;
                }

                String music = musicOption.getAsString();
                music = music.contains("http") ? music : "spsearch: " + music;

                Consumer<AudioTrack> consumer = track -> {
                    if (track == null) {
                        hook.sendMessage("No music found").queue();
                        return;
                    }

                    PlaylistTrackInfo trackInfo = new PlaylistTrackInfo(
                            event.getUser().getName(),
                            event.getUser().getIdLong(),
                            track.getInfo().title,
                            track.getInfo().uri,
                            track.getDuration()
                    );

                    playlist.add(trackInfo);
                    playlist.save();

                    EmbedBuilder embed = new EmbedBuilder()
                            .setTitle("💿 " + event.getUser().getName() + " added to playlist")
                            .setColor(EmbedUtil.getColor())
                            .setDescription(
                                    "\ud83d\udcc0 Name: `" + track.getInfo().title + "`\n" +
                                            "\uD83D\uDCB0 Author: `" + track.getInfo().author + "`\n" +
                                            "\uD83D\uDCCC Link: [Click here](" + track.getInfo().uri + ")"
                            );


                    hook.sendMessageEmbeds(embed.build()).queue();
                };

                loadTrack(MusicSearchType.ADDING_TO_PLAYLIST, music, event.getMember(), hook, consumer);

                return;
            }

            if (subcommandName.equalsIgnoreCase("remove")) {
                OptionMapping indexOption = event.getOption("index");
                if (indexOption == null) {
                    hook.sendMessage("No index specified").queue();
                    return;
                }

                int index = (int) indexOption.getAsLong();

                PlaylistTrackInfo trackInfo = playlist.remove(index);
                if (trackInfo == null) {
                    hook.sendMessage("No song found at index " + index).queue();
                    return;
                }

                playlist.save();

                hook.sendMessage("Removed song " + trackInfo.trackName() + "!").queue();
                return;
            }

            if (subcommandName.equalsIgnoreCase("clear")) {
                playlist.clear();
                playlist.save();

                hook.sendMessage("Cleared the playlist!").queue();
                return;
            }

            if (subcommandName.equalsIgnoreCase("autoplay")) {
                playlist.setAutoPlay(!playlist.isAutoPlay());
                playlist.save();

                hook.sendMessage("Autoplay is now " + (playlist.isAutoPlay() ? "enabled" : "disabled") + "!").queue();
                return;
            }

            if (subcommandName.equalsIgnoreCase("shuffle")) {
                playlist.shuffle();
                playlist.setCurrentIndex(0);

                playlist.save();

                StringBuilder sb = new StringBuilder();

                int index = 1;
                for (PlaylistTrackInfo track : playlist.getTracks()) {
                    sb.append(index).append(". ").append(track).append("\n");
                    index++;
                }

                if (!sb.isEmpty()) {
                    sb.setLength(sb.length() - 1);
                } else {
                    sb.append("No songs in the playlist");
                }

                EmbedBuilder embed = EmbedUtil.createDefaultEmbed(sb.toString())
                        .setTitle("Shuffled the playlist!");

                hook.sendMessageEmbeds(embed.build()).queue();
                return;
            }

            hook.sendMessage("Unknown subcommand").queue();
        });
    }

    @NonNull
    public CustomPlaylist getCustomPlaylist(long guildId) {
        CustomPlaylist customPlaylist = customPlaylistByGuildId.get(guildId);
        if (customPlaylist == null) {
            customPlaylist = new CustomPlaylist(guildId);
            customPlaylistByGuildId.put(guildId, customPlaylist);
        }

        return customPlaylist;
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
    public void onMenuSelection(StringSelectInteractionEvent event) {
        if (event.getGuild() == null || event.getMember() == null) return;
        if (!event.getComponentId().startsWith("music-select-")) return;

        String id = event.getComponentId().replace("music-select-", "");

        event.deferEdit().queue(hook -> {
            getByGuildId(event.getGuild()).queue(player -> {
                SelectOption selectOption = event.getSelectedOptions().get(0);
                int index = Integer.parseInt(selectOption.getValue()) - 1;

                List<AudioTrack> tracks = searchCache.remove(id);
                if (tracks == null) {
                    hook.editOriginal("❌ `The search result has been expired, try again`")
                            .setComponents()
                            .setEmbeds()
                            .queue();
                    return;
                }

                AudioTrack track = tracks.get(index);

                MusicSearchEngine.builder()
                        .hook(hook)
                        .member(event.getMember())
                        .player(player)
                        .searchType(MusicSearchType.SIMPLE_SEARCH)
                        .build()
                        .trackLoaded(track);
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

    public void loadTrack(MusicSearchType searchType, String input, Member member, @Nullable InteractionHook hook, Consumer<AudioTrack> trackFoundConsumer) {
        LoggerUtil.getLogger().info("Loading track " + input + " [" + searchType + "] requested by " + member.getUser().getName());

        getByGuildId(member.getGuild()).queue(player -> {
            val handlerBuilder = MusicSearchEngine.builder()
                    .player(player)
                    .trackUrl(input)
                    .member(member)
                    .searchType(searchType);

            if (hook != null) {
                player.setTextChannelId(hook.getInteraction().getChannelIdLong());
            }

            if (searchType == MusicSearchType.ADDING_TO_PLAYLIST) {
                handlerBuilder.trackFoundConsumer(trackFoundConsumer);
            }

            if (searchType != MusicSearchType.LOOKING_PLAYLIST && hook != null) {
                handlerBuilder.hook(hook);
                audioManager.loadItemOrdered(player, input, handlerBuilder.build());
                return;
            }

            audioManager.loadItemOrdered(player, input, handlerBuilder.build());
        });
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
                            stringBuilder.append(trackManager.getPlayer().isPaused() ? "⏸" : "▶")
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

    public void sendSearchResult(String trackUrl, List<AudioTrack> tracks, InteractionHook hook) {
        try {
            List<AudioTrack> audioTracks = tracks.subList(0, 10);


        EmbedBuilder embed = EmbedUtil.createDefaultEmbed("");

        StringBuilder musics = new StringBuilder();
        musics.append("### 🔍 Procurando por `")
                .append(trackUrl.replace("spsearch: ", ""))
                .append("`");

        for (int i = 0; i < audioTracks.size(); i++) {
            AudioTrack track = audioTracks.get(i);
            musics.append("\n**")
                    .append(i + 1)
                    .append(".** [`")
                    .append(MusicUtil.getTimeStamp(track.getDuration()))
                    .append("`] ")
                    .append(track.getInfo().title)
                    .append(" de ")
                    .append(track.getInfo().author);
        }

        embed.setDescription(musics.toString());

        String id = UUID.randomUUID().toString().substring(0, 5);

        StringSelectMenu.Builder builder = StringSelectMenu.create("music-select-" + id)
                .setPlaceholder("Selecione a música que deseja adicionar")
                .setMinValues(1)
                .setMaxValues(1);

        for (int i = 0; i < audioTracks.size(); i++) {
            AudioTrack track = audioTracks.get(i);

            String data = "[" + MusicUtil.getTimeStamp(track.getDuration()) + "] "
                    + track.getInfo().title
                    + " de "
                    + track.getInfo().author;

            int position = i + 1;
            builder.addOption(data, String.valueOf(position), Emoji.fromUnicode(EMOJIS[i]));
        }

        searchCache.put(id, audioTracks);

        hook.sendMessageEmbeds(embed.build())
                .addActionRow(builder.build())
                .queue();
        } catch (Exception e) {
            LoggerUtil.getLogger().severe("Error while trying to send search result");
            LoggerUtil.printException(e);
            return;
        }
    }
}
