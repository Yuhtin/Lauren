package com.yuhtin.lauren.commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import com.yuhtin.lauren.utils.helper.MathUtils;
import com.yuhtin.lauren.utils.helper.TrackUtils;
import com.yuhtin.lauren.utils.helper.Utilities;
import com.yuhtin.lauren.core.music.AudioInfo;
import com.yuhtin.lauren.core.music.AudioPlayerSendHandler;
import com.yuhtin.lauren.core.music.TrackManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Set;

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
                                            "\uD83D\uDCE2 Tipo de vídeo: `" +
                                            (track.getInfo().isStream ? "Stream" : track.getInfo().title.contains("Podcast") ?
                                                    "Podcast" : "Música") + "`\n" +
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

                    for (AudioInfo audioInfo : queue) {
                        builder.append(TrackUtils.buildQueueMessage(audioInfo));
                        totalTime += audioInfo.getTrack().getInfo().length;
                    }

                    EmbedBuilder embed = new EmbedBuilder()
                            .setTitle("\ud83d\udcbf Informações da fila [" + TrackUtils.getTimestamp(totalTime) + "]");

                    if (builder.length() <= 2001) {
                        embed.setDescription(
                                DVD + " " + queue.size() + " " + MathUtils.plural(queue.size(), "música", "músicas") + "\n\n" +
                                        builder.toString());
                        event.getChannel().sendMessage(embed.build()).queue();
                    } else {
                        try {

                            HttpResponse response = Unirest.post("https://hastebin.com/documents").body(builder.toString()).asString();

                            builder.setLength(1924);
                            embed.setDescription(
                                    DVD + " " + queue.size() + " " + MathUtils.plural(queue.size(), "música", "músicas") + "\n\n" + builder.toString() +
                                    "\n[Clique aqui para ver o resto das músicas](https://hastebin.com/" + new JSONObject(response.getBody().toString()).getString("key") + ")");

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

                    info.addSkip(event.getAuthor());
                    if (info.getSkips() >= audio.getMembers().size() - 2) {
                        event.getChannel().sendMessage("\uD83E\uDDF6 Amo quando todos concordam entre si, pulando a música").queue();
                        return;
                    }

                    event.getMessage().delete().queue();
                    event.getChannel().sendMessage("\uD83E\uDDEC **" + event.getMember().getNickname() + "** votou para pular a música **(" + info.getSkips() + "/" + (audio.getMembers().size() - 2) + ")**").queue();
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

        String input = String.join(" ", Arrays.copyOfRange(arguments, 1, arguments.length));
        switch (operation) {
            case "search":
            case "buscar":
            case "busca":
                input = "ytsearch: " + input;

            case "link":
            case "play":
            case "tocar": {
                trackManager.loadTrack(input, event.getMember(), event.getTextChannel());
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

    private void forceSkipTrack(TextChannel channel) {
        trackManager.player.stopTrack();
        channel.sendMessage("\u23e9 Pulei a música pra você <3").queue();
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