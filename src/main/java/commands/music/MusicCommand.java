package commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import core.music.AudioInfo;
import core.music.AudioPlayerSendHandler;
import core.music.TrackManager;
import models.annotations.CommandHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.json.JSONObject;
import utils.helper.Utilities;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static utils.helper.TrackUtils.buildQueueMessage;
import static utils.helper.TrackUtils.getProgressBar;

@CommandHandler(name = "tocar", type = CommandHandler.CommandType.MUSIC, description = "Colocar eu para por um som na caixa")
public class MusicCommand extends Command {

    public MusicCommand() {
        name = "tocar";
        aliases = new String[]{"music", "play", "musica"};
    }

    public static TrackManager trackManager;
    public static VoiceChannel audio = null;

    @Override
    protected void execute(CommandEvent event) {
        if (event.getArgs().isEmpty()) {
            sendHelpMessage(event.getTextChannel());
            return;
        }

        String[] arguments = event.getArgs().split(" ");
        if (arguments.length == 0) {
            sendHelpMessage(event.getTextChannel());
            return;
        }

        if (event.getMember().getVoiceState() == null || event.getMember().getVoiceState().getChannel() == null
                || event.getMember().getVoiceState().getChannel().getIdLong() != 722935562155196506L) {
            event.getChannel().sendMessage("\uD83C\uDFB6 Amiguinho, entre no canal `\uD83C\uDFB6┇Batidões` para poder usar comandos de música").queue();
            return;
        }
        createPlayer(event.getGuild(), event.getMember().getVoiceState().getChannel());

        String operation = arguments[0].toLowerCase();
        if (arguments.length == 1) {
            String DVD = "\ud83d\udcc0";
            switch (operation) {
                case "pause":
                case "pausar": {
                    if (isIdle(event.getTextChannel())) return;
                    if (!Utilities.isDJ(event.getMember(), event.getTextChannel(), true)) return;

                    trackManager.player.setPaused(!trackManager.player.isPaused());
                    if (trackManager.player.isPaused())
                        event.getChannel().sendMessage("\uD83E\uDD7A Taxaram meu batidão, espero que me liberem logo").queue();
                    else event.getChannel().sendMessage("\uD83E\uDD73 Liberaram meu batidão uhhuuuu").queue();
                    return;
                }

                case "atual":
                case "help":
                case "info": {
                    if (trackManager.player.getPlayingTrack() == null) {
                        event.getChannel().sendMessage("\uD83D\uDCCC Olha, eu não to tocando nada atualmente, que tal por som na caixa?").queue();
                        return;
                    }

                    AudioTrack track = trackManager.player.getPlayingTrack();
                    String CD = "\ud83d\udcbf";
                    EmbedBuilder embed = new EmbedBuilder()
                            .setTitle(CD + " Informações da música atual")
                            .setDescription(
                                    DVD + " Nome: `" + track.getInfo().title + "`\n" +
                                            "\uD83D\uDCB0 Autor: `" + track.getInfo().author + "`\n" +
                                            "\uD83D\uDCE2 Tipo de vídeo: `" +
                                            (track.getInfo().isStream ? "Stream" : track.getInfo().title.contains("Podcast") ?
                                                    "Podcast" : "Música") + "`\n" +
                                            "\uD83E\uDDEA Timeline: " + (trackManager.player.isPaused() ? "▶️" : "⏸") + " ⏭ " + (trackManager.player.getVolume() < 50 ? "\uD83D\uDD09" : "\uD83D\uDD0A") + " " + getProgressBar(track) + "\n" +
                                            "\uD83E\uDDEC Membro que adicionou: <@" + trackManager.getTrackInfo(track).getAuthor().getIdLong() + ">\n" +
                                            "\n" +
                                            "\uD83D\uDCCC Link: [Clique aqui](" + track.getInfo().uri + ")");

                    event.getChannel().sendMessage(embed.build()).queue();
                    return;
                }

                case "listar":
                case "lista":
                case "l": {
                    if (trackManager.getQueuedTracks().isEmpty()) {
                        event.getChannel().sendMessage("\uD83D\uDCCC Eita, não tem nenhum batidão pra tocar, adiciona uns ai <3").queue();
                        return;
                    }

                    StringBuilder builder = new StringBuilder();
                    Set<AudioInfo> queue = trackManager.getQueuedTracks();
                    queue.forEach(audioInfo -> builder.append(buildQueueMessage(audioInfo)));
                    EmbedBuilder embed = new EmbedBuilder()
                            .setTitle(DVD + " Informações da fila [" + queue.size() + "]")
                            .setDescription(builder.toString());

                    if (builder.length() <= 1959)
                        event.getChannel().sendMessage(embed.build()).queue();
                    else {
                        try {
                            HttpResponse response = Unirest.post("https://hastebin.com/documents").body(builder.toString()).asString();
                            event.getChannel().sendMessage(embed.setDescription(builder.toString() + "\n[Clique aqui para ver o resto das músicas](https://hastebin.com/" + new JSONObject(response.getBody().toString()).getString("key") + ")").build()).queue();
                        } catch (Exception exception) {
                            event.getChannel().sendMessage("❌ Eita, algo de errado não está certo, tentei criar um linkzin com as músicas da playlist pra você, mas o hastebin ta off \uD83D\uDE2D").queue();
                            return;
                        }
                    }
                    return;
                }

                case "votar":
                case "pular": {
                    if (isIdle(event.getTextChannel())) return;
                    if (isCurrentDj(event.getMember())) {
                        forceSkipTrack(event.getTextChannel());
                        return;
                    }

                    AudioInfo info = trackManager.getTrackInfo(trackManager.player.getPlayingTrack());
                    if (info.hasVoted(event.getAuthor())) {
                        event.getChannel().sendMessage("\uD83D\uDC6E\uD83C\uDFFD\u200D♀️ Ei você já votou pra pular essa música ;-;").queue();
                        return;
                    }

                    if (info.getSkips() >= audio.getMembers().size() - 2) {
                        event.getChannel().sendMessage("\uD83E\uDDF6 Amo quando todos concordam entre si, pulando a música").queue();
                        return;
                    }

                    info.addSkip(event.getAuthor());
                    event.getMessage().delete().queue();
                    event.getChannel().sendMessage("\uD83E\uDDEC **" + event.getMember().getNickname() + "** votou para pular a música **(" + info.getSkips() + "/" + (audio.getMembers().size() - 1) + ")**").queue();
                    return;
                }

                case "fpular":
                case "fp": {
                    if (isIdle(event.getTextChannel())) return;
                    if (!Utilities.isDJ(event.getMember(), event.getTextChannel(), true)) return;

                    forceSkipTrack(event.getTextChannel());
                    return;
                }

                case "limpar":
                case "sair":
                case "leave": {
                    if (!Utilities.isDJ(event.getMember(), event.getTextChannel(), true)) return;

                    trackManager.player.destroy();
                    trackManager.purgeQueue();
                    event.getGuild().getAudioManager().closeAudioConnection();
                    event.getChannel().sendMessage("Que ⁉️ Pensei que estavam gostando do batidão \uD83D\uDC94 Prometo que da próxima será melhor").queue();
                    return;
                }

                case "misture":
                case "misturar":
                case "m": {
                    if (isIdle(event.getTextChannel())) return;
                    if (!Utilities.isDJ(event.getMember(), event.getTextChannel(), true)) return;

                    trackManager.shuffleQueue();
                    event.getChannel().sendMessage("<a:infinito:703187274912759899> Misturando a lista de músicas").queue();
                    return;
                }
            }
        }

        if (arguments.length == 2) {
            String input = String.join(" ", Arrays.copyOfRange(arguments, 1, arguments.length));
            switch (operation) {
                case "search":
                case "buscar":
                    input = "ytsearch: " + input;

                case "link":
                case "play":
                case "tocar": {
                    trackManager.loadTrack(input, event.getMember(), event.getMessage(), event.getTextChannel());
                    return;
                }
            }
        }

        String input = String.join(" ", Arrays.copyOfRange(arguments, 2, arguments.length));
        switch (operation) {
            case "jump":
            case "teleport": {
                String urlLink = input;
                if (arguments[1].equalsIgnoreCase("buscar")) urlLink = "ytsearch: " + urlLink;
                final AudioTrack[] tracks = {null};
                trackManager.audioManager.loadItemOrdered(trackManager.musicManager, urlLink, new AudioLoadResultHandler() {
                    @Override
                    public void trackLoaded(AudioTrack audioTrack) {
                        tracks[0] = audioTrack;
                    }

                    @Override
                    public void playlistLoaded(AudioPlaylist playlist) {
                        if (playlist.getSelectedTrack() != null) trackLoaded(playlist.getSelectedTrack());
                        else if (playlist.isSearchResult()) trackLoaded(playlist.getTracks().get(0));
                        else
                            event.getChannel().sendMessage(":anger: Playlist não são suportadas neste comando").queue();
                    }

                    @Override
                    public void noMatches() {
                        event.getChannel().sendMessage("\uD83D\uDC94 Como assim??? Você quer quebrar meus sistemas? \uD83D\uDE2D")
                                .queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
                        event.getChannel().sendMessage("\uD83D\uDCCC Não consegui encontrar nada relacionado ao que me enviou :p")
                                .queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
                    }

                    @Override
                    public void loadFailed(FriendlyException exception) {
                        exception.printStackTrace();
                        event.getChannel().sendMessage("\uD83D\uDC94 Como assim??? Você quer quebrar meus sistemas? \uD83D\uDE2D")
                                .queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
                        event.getChannel().sendMessage("\uD83D\uDCCC Esse formato de arquivo não é valido \uD83D\uDEE9")
                                .queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
                    }
                });

                if (tracks[0] == null) {
                    event.getChannel().sendMessage(":pleading_face: Não encontrei nada sobre o que me enviou").queue();
                    return;
                }

                AudioTrack track = tracks[0];
                AudioInfo trackInfo = trackManager.getTrackInfo(track);
                if (trackInfo == null) {
                    EmbedBuilder embed = new EmbedBuilder()
                            .setTitle("💿 " + Utilities.getFullName(event.getMember().getUser()) + " adicionou 1 música a fila")
                            .setDescription(
                                    "\ud83d\udcc0 Nome: `" + track.getInfo().title + "`\n" +
                                            "\uD83D\uDCB0 Autor: `" + track.getInfo().author + "`\n" +
                                            "\uD83D\uDCE2 Tipo de vídeo: `" +
                                            (track.getInfo().isStream ? "Stream" : track.getInfo().title.contains("Podcast") ?
                                                    "Podcast" : "Música") + "`\n" +
                                            "\uD83D\uDCCC Link: [Clique aqui](" + track.getInfo().uri + ")");

                    trackManager.play(track, event.getMember());
                    event.getChannel().sendMessage(embed.build()).queue();
                    event.getChannel().sendMessage(":pleading_face: Não encontrei a música que pediu então adicionei ela na lista").queue();
                    return;
                }

                trackManager.play(track, event.getMember());
                trackManager.player.playTrack(track);
                event.getChannel().sendMessage("\u23e9 Pulei para a música `" + track.getInfo().title + "` pra você <3").queue();
                return;
            }
        }

        sendHelpMessage(event.getTextChannel());
    }

    private void createPlayer(Guild guild, VoiceChannel channel) {
        if (trackManager != null) return;

        audio = channel;
        trackManager = new TrackManager();
        trackManager.player.addListener(trackManager);

        guild.getAudioManager().setSendingHandler(new AudioPlayerSendHandler(trackManager.player));
    }

    private boolean isCurrentDj(Member member) {
        return trackManager.getTrackInfo(trackManager.player.getPlayingTrack()).getAuthor().equals(member);
    }

    private boolean isIdle(TextChannel channel) {
        if (trackManager.player.getPlayingTrack() == null) {
            channel.sendMessage("Amigo, eu não to tocando nada não '-'").queue();
            return true;
        }

        return false;
    }

    private void forceSkipTrack(TextChannel channel) {
        trackManager.player.stopTrack();
        channel.sendMessage("\u23e9 Pulei a música pra você <3").queue();
    }

    private void sendHelpMessage(TextChannel chat) {
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle("♨️ Vamo agitar um flow pesadão?")
                .setDescription("Todos \uD83C\uDF20 - _Aqui são os comandos liberados a todos os jogadores_\n" +
                        "$tocar play [link da música] | Carrega uma música ou playlist \n" +
                        "$tocar buscar [nome da música] | Procure no YouTube um vídeo pelo nome \n" +
                        "$tocar lista | Veja a fila atual de músicas do servidor \n" +
                        "$tocar pular | Execute um voto para ignorar a faixa atual \n" +
                        "$tocar info | Exibir informações relacionadas à faixa atual \n" +
                        "$tocar pausar | Pausar a minha música atual\n" +
                        "\n" +
                        "DJ \uD83C\uDF99 - _Abaixo são comandos apenas para meus produtores_\n" +
                        "$tocar fpular | Pule a música atual sem precisar de voto \n" +
                        "$tocar limpar | Limpar a fila de músicas\n" +
                        "$tocar misturar | Misturar as faixas da playlist\n" +
                        "$tocar teleport <buscar ou link> <link ou nome> | Pular para uma música da lista")
                .setThumbnail("https://i.pinimg.com/originals/c4/1d/e9/c41de98f6fd11ca86b897763fbfb4559.gif");

        chat.sendMessage(builder.build()).queue();
    }


}