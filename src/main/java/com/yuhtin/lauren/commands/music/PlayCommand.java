package com.yuhtin.lauren.commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.model_objects.specification.Track;
import com.yuhtin.lauren.Lauren;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.music.TrackManager;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import com.yuhtin.lauren.utils.helper.TrackUtils;
import com.yuhtin.lauren.utils.helper.Utilities;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.Arrays;

@CommandHandler(
        name = "tocar",
        type = CommandHandler.CommandType.MUSIC,
        description = "Tocar algum somzinho ai",
        alias = {"play"}
)
public class PlayCommand extends Command {

    final String trackIndentifier = "/track/",
            playlistIndentifier = "/playlist/";

    @SneakyThrows
    @Override
    protected void execute(CommandEvent event) {
        if (!TrackUtils.get().isInVoiceChannel(event.getMember())) {
            event.getChannel().sendMessage("\uD83C\uDFB6 Amiguinho, entre no canal `\uD83C\uDFB6┇Batidões` para poder usar comandos de música").queue();
            return;
        }

        if (!TrackUtils.get().isIdle(null) && !TrackManager.get().audio.equals(event.getMember().getVoiceState().getChannel())) {
            event.getChannel().sendMessage("\uD83C\uDFB6 Você precisa estar no mesmo canal que eu para usar isto").queue();
            return;
        }

        String[] arguments = event.getArgs().split(" ");
        if (arguments.length == 0) {
            event.getChannel().sendMessage("<a:nao:704295026036834375> Utilize :clock9: `$p <link ou nome>`").queue();
            return;
        }

        String input = String.join(" ", Arrays.copyOfRange(arguments, 0, arguments.length));
        input = input.contains("http") ? input : "ytsearch: " + input;

        if (input.contains("spotify.com") && Lauren.spotifyApi != null) {
            String url = input.contains(trackIndentifier) ?
                    input.split(trackIndentifier)[1].replace("?si", "").split("=")[0]
                    : input.split(playlistIndentifier)[1].replace("?si", "").split("=")[0];

            if (input.contains(trackIndentifier)) {
                Track track = Lauren.spotifyApi.getTrack(url).build().execute();
                input = "ytsearch: " + track.getName() + " " + track.getArtists()[0].getName();

            } else if (input.contains(playlistIndentifier)) {
                try {
                    Playlist playlist = Lauren.spotifyApi.getPlaylist(url).build().execute();

                    int limit = Utilities.INSTANCE.isBooster(event.getMember()) || Utilities.INSTANCE.isDJ(event.getMember(), null, false) ? 100 : 25;
                    int maxMusics = Math.min(playlist.getTracks().getItems().length, limit);

                    for (int i = 0; i < maxMusics; i++) {
                        Track track = (Track) playlist.getTracks().getItems()[i].getTrack();
                        TrackManager.get().loadTrack("ytsearch: " + track.getName() + " " + track.getArtists()[0].getName(),
                                event.getMember(),
                                event.getTextChannel(),
                                false);
                    }

                    EmbedBuilder embed = new EmbedBuilder()
                            .setTitle("💿 " + Utilities.INSTANCE.getFullName(event.getMember().getUser()) + " adicionou " + maxMusics + " músicas a fila")
                            .setDescription("\uD83D\uDCBD Informações da playlist:\n" +
                                    "\ud83d\udcc0 Nome: `" + playlist.getName() + "`\n" +
                                    "\uD83C\uDFB6 Músicas: `" + maxMusics + "`\n\n" +
                                    "\uD83D\uDCCC Link: [Clique aqui](" + input + ")");

                    Logger.log("The player " + Utilities.INSTANCE.getFullName(event.getMember().getUser()) + " added a playlist with " + maxMusics + " musics").save();
                    event.getChannel().sendMessage(embed.build()).queue();
                    return;
                } catch (Exception ignored) { }
            }
        }

        TrackManager.get().loadTrack(input, event.getMember(), event.getTextChannel(), true);
    }
}
