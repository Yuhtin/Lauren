package com.yuhtin.lauren.commands.impl.music;

import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.core.music.TrackManager;
import com.yuhtin.lauren.commands.CommandData;
import com.yuhtin.lauren.utils.helper.TrackUtils;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

@CommandData(
        name = "musica",
        type = CommandData.CommandType.MUSIC,
        description = "Ver as informações da música atual"
)
public class MusicCommand implements Command {

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) throws Exception {
        if (event.getGuild() == null
                || event.getMember() == null
                || TrackUtils.get().isIdle(event.getGuild(), hook)) return;

        val trackManager = TrackManager.of(event.getGuild());

        val track = trackManager.getPlayer().getPlayingTrack();
        val isRepeating = trackManager.getTrackInfo().isRepeat() ? "`Ativa`" : "`Desativada`";

        val embed = new EmbedBuilder();
        embed.setTitle("\ud83d\udcbf Informações da música atual");
        embed.setDescription("\ud83d\udcc0 Nome: `" + track.getInfo().title + "`\n" +
                "\uD83D\uDCB0 Autor: `" + track.getInfo().author + "`\n" +
                "\uD83D\uDCE2 Tipo de vídeo: `" + (track.getInfo().isStream ? "Stream" : track.getInfo().title.contains("Podcast") ? "Podcast" : "Música") + "`\n" +
                "<a:infinito:703187274912759899> Repetição: " + isRepeating + "\n" +
                "\uD83E\uDDEC Membro que adicionou: <@" + trackManager.getTrackInfo().getAuthor().getIdLong() + ">\n" +
                "\uD83E\uDDEA Timeline: " + "⏸ ⏭ \uD83D\uDD0A " + TrackUtils.get().getProgressBar(track) + "\n" +
                "\n" +
                "\uD83D\uDCCC Link: [Clique aqui](" + track.getInfo().uri + ")");

        hook.sendMessageEmbeds(embed.build()).queue();
    }

}
