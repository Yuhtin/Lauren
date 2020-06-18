package commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import models.annotations.CommandHandler;
import music.AudioInfo;
import music.AudioPlayerSendHandler;
import music.TrackManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.json.JSONObject;
import utils.helper.Utilities;

import java.util.Arrays;
import java.util.Set;

import static utils.helper.TrackUtils.buildQueueMessage;
import static utils.helper.TrackUtils.getProgressBar;
import static utils.helper.TrackUtils.getTimestamp;

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

        createPlayer(event.getGuild());

        String[] arguments = event.getArgs().split(" ");
        if (arguments.length == 0) {
            sendHelpMessage(event.getTextChannel());
            return;
        }

        String operation = arguments[0].toLowerCase();
        if (arguments.length == 1) {
            String DVD = "\ud83d\udcc0";
            switch (operation) {
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
                                    "\uD83E\uDDEA Tempo tocado: " + getProgressBar(track) + "\n" +
                                    "\uD83E\uDDEC Membro que adicionou: <@" + trackManager.getTrackInfo(track).getAuthor().getIdLong() + ">\n" +
                                    "\n" +
                                    "\uD83D\uDCCC Link: [Clique aqui](" + track.getInfo().uri + ")");

                    event.getChannel().sendMessage(embed.build()).queue();
                    return;
                }

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

                    if (info.getSkips() >= audio.getMembers().size() - 1) {
                        event.getChannel().sendMessage("\uD83E\uDDF6 Amo quando todos concordam entre si, pulando a música").queue();
                        return;
                    }

                    info.addSkip(event.getAuthor());
                    event.getMessage().delete().queue();
                    event.getChannel().sendMessage("\uD83E\uDDEC **" + event.getMember().getNickname() + "** votou para pular a música **(" + info.getSkips() + "/" + (audio.getMembers().size() - 1) + "**").queue();
                    return;
                }
                case "fpular":
                case "fp": {
                    if (isIdle(event.getTextChannel())) return;

                    if (Utilities.isDJ(event.getMember())) {
                        forceSkipTrack(event.getTextChannel());
                        return;
                    }

                    event.getChannel().sendMessage("Ahhh, que pena \uD83D\uDC94 você não pode realizar essa operação").queue();
                    return;
                }
                case "sair":
                case "leave": {
                    if (!Utilities.isDJ(event.getMember())) {
                        event.getChannel().sendMessage("Ahhh, que pena \uD83D\uDC94 você não pode realizar essa operação").queue();
                        return;
                    }

                    trackManager.player.destroy();
                    trackManager.purgeQueue();
                    event.getGuild().getAudioManager().closeAudioConnection();
                    event.getChannel().sendMessage("Que ⁉️ Pensei que estavam gostando do batidão \uD83D\uDC94 Prometo que da próxima será melhor").queue();
                    return;
                }
                case "misturar":
                case "m": {
                    if (isIdle(event.getTextChannel())) return;

                    if (Utilities.isDJ(event.getMember())) {
                        trackManager.shuffleQueue();
                        event.getChannel().sendMessage("<a:infinito:703187274912759899> Misturando a lista de músicas").queue();
                        return;
                    }

                    event.getChannel().sendMessage("Ahhh, que pena \uD83D\uDC94 você não pode realizar essa operação").queue();
                    return;
                }
            }
        }

        String input = String.join(" ", Arrays.copyOfRange(arguments, 1, arguments.length));
        switch (operation) {
            case "buscar": input = "ytsearch: " + input;

            case "play":
            case "tocar": {
                trackManager.loadTrack(input, event.getMember(), event.getMessage(), event.getTextChannel());
                return;
            }
        }

        sendHelpMessage(event.getTextChannel());
    }

    private void createPlayer(Guild guild) {
        if (trackManager != null) return;

        trackManager = new TrackManager();
        audio = guild.getVoiceChannelById(722935562155196506L);
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
                .setTitle(":bookmark: Comandos de Música")
                .setDescription(
                        "\n" +
                                "$tocar play [link da música] | Carrega uma música ou playlist \n" +
                                "$tocar buscar [nome da música] | Procure no YouTube um vídeo pelo nome \n" +
                                "$tocar lista | Veja a fila atual de músicas do servidor \n" +
                                "$tocar pular | Execute um voto para ignorar a faixa atual \n" +
                                "$tocar info | Exibir informações relacionadas à faixa atual \n" +
                                "\n" +
                                "\uD83D\uDC8E Os comandos abaixo são exclusivos para DJ \uD83D\uDC8E \n" +
                                "\n" +
                                "$tocar fpular | Pule a música atual sem precisar de voto \n" +
                                "$tocar sair | Desconectar o bot do canal atual\n" +
                                "$tocar misturar | Misturar as faixas da playlist");

        chat.sendMessage(builder.build()).queue();
    }


}
