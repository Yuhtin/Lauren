package com.yuhtin.lauren.commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.model_objects.specification.Track;
import com.yuhtin.lauren.application.Lauren;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.music.AudioInfo;
import com.yuhtin.lauren.core.music.AudioPlayerSendHandler;
import com.yuhtin.lauren.core.music.TrackManager;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import com.yuhtin.lauren.service.ConnectionFactory;
import com.yuhtin.lauren.utils.helper.MathUtils;
import com.yuhtin.lauren.utils.helper.TrackUtils;
import com.yuhtin.lauren.utils.helper.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.util.*;

@CommandHandler(
        name = "tocar",
        type = CommandHandler.CommandType.MUSIC,
        description = "Colocar eu para por um som na caixa",
        alias = {"m", "music", "musica", "play"})
public class MusicCommand extends Command {

    public static final Map<String, String> fields = new HashMap<>();
    public static TrackManager trackManager;
    public static VoiceChannel audio = null;


    public MusicCommand() {
        name = "tocar";
        aliases = new String[]{"m", "music", "play", "musica"};
    }

    public static void constructFields() {
        fields.put("api_dev_key", Lauren.config.pastebinDevKey);
        fields.put("api_user_key", Lauren.config.pastebinUserKey);
        fields.put("api_paste_private", "1");
        fields.put("api_paste_expire_date", "10M");
        fields.put("api_paste_format", "yaml");
        fields.put("api_option", "paste");
        fields.put("api_paste_code", "");
        fields.put("api_paste_name", "");
    }

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

        if (!Utilities.isOwner(event.getChannel(), event.getAuthor(), false)
                && (event.getMember().getVoiceState() == null
                || event.getMember().getVoiceState().getChannel() == null
                || event.getMember().getVoiceState().getChannel().getIdLong() != 722935562155196506L)) {
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
                        event.getChannel().sendMessage("\uD83D\uDCCC Eita, não tem nenhum batidão pra tocar, adiciona uns ai <3").queue();
                        return;
                    }

                    AudioTrack track = trackManager.player.getPlayingTrack();
                    String CD = "\ud83d\udcbf";
                    EmbedBuilder embed = new EmbedBuilder()
                            .setTitle(CD + " Informações da música atual")
                            .setDescription(
                                    DVD + " Nome: `" + track.getInfo().title + "`\n" +
                                            "\uD83D\uDCB0 Autor: `" + track.getInfo().author + "`\n" +
                                            "\uD83D\uDCE2 Tipo de vídeo: `" + (track.getInfo().isStream ? "Stream" : track.getInfo().title.contains("Podcast") ? "Podcast" : "Música") + "`\n" +
                                            "\uD83E\uDDEC Membro que adicionou: <@" + trackManager.getTrackInfo(track).getAuthor().getIdLong() + ">\n" +
                                            "\uD83E\uDDEA Timeline: " + (trackManager.player.isPaused() ? "▶️" : "⏸") + " ⏭ " + (trackManager.player.getVolume() < 50 ? "\uD83D\uDD09" : "\uD83D\uDD0A") + " " + TrackUtils.getProgressBar(track) + "\n" +
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
                    long totalTime = 0;

                    List<String> users = new ArrayList<>();
                    for (AudioInfo audioInfo : queue) {
                        builder.append(TrackUtils.buildQueueMessage(audioInfo));
                        totalTime += audioInfo.getTrack().getInfo().length;

                        String userId = audioInfo.getAuthor().getId();
                        if (!users.contains(userId)) users.add(userId);
                    }

                    EmbedBuilder embed = new EmbedBuilder()
                            .setTitle("\ud83d\udcbf Informações da fila [" + TrackUtils.getTimeStamp(totalTime) + "]");

                    if (builder.length() <= 2001) {
                        users.clear();
                        embed.setDescription(
                                DVD + " " + MathUtils.plural(queue.size(), "música", "músicas") + "\n\n" +
                                        builder.toString());
                        event.getChannel().sendMessage(embed.build()).queue();
                    } else {
                        try {
                            String content = builder.toString();

                            content = content.replace("`", "");
                            for (int i = 0; i < users.size(); i++)
                                content = content.replace("(<@" + users.get(i) + ">)", "");

                            fields.replace("api_paste_code", content);
                            fields.replace("api_paste_name", "Lauren playlist (" + queue.size() + " musicas)");

                            ConnectionFactory factory = new ConnectionFactory(fields, "https://pastebin.com/api/api_post.php");
                            builder.setLength(1924);
                            embed.setDescription(DVD + " " + MathUtils.plural(queue.size(), "música", "músicas") + "\n\n" + builder.toString()
                                    + "\n[Clique aqui para ver o resto das músicas](" + factory.buildConnection() + ")");

                            event.getChannel().sendMessage(embed.build()).queue();
                        } catch (Exception exception) {
                            event.getChannel().sendMessage(exception.getMessage()).queue();
                            event.getChannel().sendMessage("❌ Eita, algo de errado não está certo, tentei criar um linkzin com as músicas da playlist pra você, mas o hastebin ta off \uD83D\uDE2D").queue();
                            return;
                        }
                    }
                    return;
                }

                case "votar":
                case "p":
                case "pular": {
                    if (isIdle(event.getTextChannel())) return;
                    if (isCurrentDj(event.getMember())) {
                        forceSkipTrack();
                        event.getChannel().sendMessage("\u23e9 Pulei a música pra você <3").queue();
                        return;
                    }

                    AudioInfo info = trackManager.getTrackInfo(trackManager.player.getPlayingTrack());
                    if (info.hasVoted(event.getAuthor())) {
                        event.getChannel().sendMessage("\uD83D\uDC6E\uD83C\uDFFD\u200D♀️ Ei você já votou pra pular essa música ;-;").queue();
                        return;
                    }

                    info.addSkip(event.getAuthor());
                    if (info.getSkips() >= audio.getMembers().size() - 2) {
                        forceSkipTrack();
                        event.getChannel().sendMessage("\uD83E\uDDF6 Amo quando todos concordam entre si, pulando a música").queue();
                        return;
                    }

                    event.getMessage().delete().queue();
                    String name = event.getMember().getNickname() == null ? Utilities.getFullName(event.getAuthor()) : event.getMember().getNickname();
                    event.getChannel().sendMessage("\uD83E\uDDEC **" + name + "** votou para pular a música **(" + info.getSkips() + "/" + (audio.getMembers().size() - 2) + ")**").queue();
                    return;
                }

                case "forcepular":
                case "fpular":
                case "fp": {
                    if (isIdle(event.getTextChannel())) return;
                    if (!Utilities.isDJ(event.getMember(), event.getTextChannel(), true)) return;

                    forceSkipTrack();
                    event.getChannel().sendMessage("\u23e9 Pulei a música pra você <3").queue();

                    return;
                }

                case "limpar":
                case "sair":
                case "leave": {
                    if (!Utilities.isDJ(event.getMember(), event.getTextChannel(), true)) return;

                    trackManager.purgeQueue();
                    trackManager.player.stopTrack();

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

        String input = String.join(" ", Arrays.copyOfRange(arguments, 1, arguments.length));
        input = input.contains("http") ? input : "ytsearch: " + input;

        if (input.contains("spotify.com") && Lauren.spotifyApi != null) {
            String url;
            if (input.contains("/track/")) {
                url = input.split("/track/")[1].replace("?si", "").split("=")[0];

                try {
                    Track track = Lauren.spotifyApi.getTrack(url).build().execute();
                    input = "ytsearch: " + track.getName() + " " + track.getArtists()[0].getName();
                } catch (Exception exception) {
                    exception.printStackTrace();
                    Logger.error(exception);
                }
            } else if (input.contains("/playlist/")) {
                url = input.split("/playlist/")[1].replace("?si", "").split("=")[0];

                try {
                    Playlist playlist = Lauren.spotifyApi.getPlaylist(url).build().execute();

                    int limit = Utilities.isBooster(event.getMember()) || Utilities.isDJ(event.getMember(), null, false) ? 100 : 25;
                    int maxMusics = Math.min(playlist.getTracks().getItems().length, limit);

                    for (int i = 0; i < maxMusics; i++) {
                        Track track = (Track) playlist.getTracks().getItems()[i].getTrack();
                        trackManager.loadTrack("ytsearch: " + track.getName() + " " + track.getArtists()[0].getName(),
                                event.getMember(),
                                event.getTextChannel(),
                                false);
                    }

                    EmbedBuilder embed = new EmbedBuilder()
                            .setTitle("💿 " + Utilities.getFullName(event.getMember().getUser()) + " adicionou " + maxMusics + " músicas a fila")
                            .setDescription("\uD83D\uDCBD Informações da playlist:\n" +
                                    "\ud83d\udcc0 Nome: `" + playlist.getName() + "`\n" +
                                    "\uD83C\uDFB6 Músicas: `" + maxMusics + "`\n\n" +
                                    "\uD83D\uDCCC Link: [Clique aqui](" + input + ")");

                    Logger.log("The player " + Utilities.getFullName(event.getMember().getUser()) + " added a playlist with " + maxMusics + " musics").save();
                    event.getChannel().sendMessage(embed.build()).queue();
                    return;
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }

        switch (operation) {
            case "search":
            case "buscar":
            case "busca":
            case "link":
            case "b":
            case "play":
            case "tocar": {
                trackManager.loadTrack(input, event.getMember(), event.getTextChannel(), true);
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
            channel.sendMessage("\uD83D\uDCCC Eita, não tem nenhum batidão pra tocar, adiciona uns ai <3").queue();
            return true;
        }

        return false;
    }

    private void forceSkipTrack() {
        trackManager.player.stopTrack();
    }

    private void sendHelpMessage(TextChannel chat) {
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle("♨️ Vamo agitar um flow pesadão?")
                .setDescription("Todos \uD83D\uDC7E - _Aqui são os comandos liberados a todos os jogadores_\n\n" +
                        "$tocar play [link da música] | Carrega uma música ou playlist \n" +
                        "$tocar buscar [nome da música] | Procure no YouTube um vídeo pelo nome \n" +
                        "$tocar lista | Veja a fila atual de músicas do servidor \n" +
                        "$tocar pular | Execute um voto para ignorar a faixa atual \n" +
                        "$tocar info | Exibir informações relacionadas à faixa atual \n" +
                        "$tocar pausar | Pausar a minha música atual\n" +
                        "\n" +
                        "DJ \uD83C\uDF99 - _Abaixo são comandos apenas para meus produtores_\n\n" +
                        "$tocar fpular | Pule a música atual sem precisar de voto \n" +
                        "$tocar limpar | Limpar a fila de músicas\n" +
                        "$tocar misturar | Misturar as faixas da playlist\n")
                .setImage("https://i.pinimg.com/originals/c4/1d/e9/c41de98f6fd11ca86b897763fbfb4559.gif");

        chat.sendMessage(builder.build()).queue();
    }

}