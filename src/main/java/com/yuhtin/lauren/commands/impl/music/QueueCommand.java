package com.yuhtin.lauren.commands.impl.music;

import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.core.music.AudioInfo;
import com.yuhtin.lauren.core.music.TrackManager;
import com.yuhtin.lauren.commands.CommandData;
import com.yuhtin.lauren.startup.Startup;
import com.yuhtin.lauren.utils.Paginator;
import com.yuhtin.lauren.utils.TrackUtils;
import lombok.Getter;
import lombok.val;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@CommandData(
        name = "queue",
        type = CommandData.CommandType.MUSIC,
        description = "Ver as músicas que eu ainda vou tocar",
        args = {
                "[pagina]-Ver uma página específica da queue"
        }
)
public class QueueCommand implements Command {

    @Getter private static final Paginator.Builder BUILDER = new Paginator.Builder()
            .setColumns(1)
            .setFinalAction(message -> message.clearReactions().queue())
            .setItemsPerPage(10)
            .setEventWaiter(Startup.getLauren().getEventWaiter())
            .useNumberedItems(true)
            .showPageNumbers(true)
            .wrapPageEnds(true)
            .setTimeout(1, TimeUnit.MINUTES);

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) throws Exception {
        if (event.getMember() == null || event.getGuild() == null) return;

        TrackManager trackManager = TrackManager.of(event.getGuild());
        if (trackManager.getQueuedTracks().isEmpty()) {
            event.getChannel().sendMessage("\uD83D\uDCCC Eita, não tem nenhum batidão pra tocar, adiciona uns ai <3").queue();
            return;
        }

        val pageOption = event.getOption("pagina");
        val page = pageOption == null ? 1: (int) pageOption.getAsDouble();

        Set<AudioInfo> queue = trackManager.getQueuedTracks();
        String[] songs = new String[queue.size()];
        long totalTime = 0;

        int i = 0;
        for (AudioInfo audioInfo : queue) {
            totalTime += audioInfo.getTrack().getInfo().length;
            songs[i] = audioInfo.toString();

            ++i;
        }

        String timeInLetter = TrackUtils.getTimeStamp(totalTime);
        BUILDER.setText((number, number2) -> {
                    StringBuilder stringBuilder = new StringBuilder();
                    if (trackManager.getPlayer().getPlayingTrack() != null) {

                        stringBuilder.append(trackManager.getPlayer().isPaused() ? "\u23F8" : "\u25B6")
                                .append(" **")
                                .append(trackManager.getPlayer().getPlayingTrack().getInfo().title)
                                .append("**")
                                .append(" - ")
                                .append("`")
                                .append(TrackUtils.getTimeStamp(trackManager.getPlayer().getPlayingTrack().getPosition()))
                                .append(" / ")
                                .append(TrackUtils.getTimeStamp(trackManager.getPlayer().getPlayingTrack().getInfo().length))
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
                .setUsers(event.getUser())
                .setColor(event.getMember().getColor());

        BUILDER.build().paginate(event.getChannel(), page);
    }
}
