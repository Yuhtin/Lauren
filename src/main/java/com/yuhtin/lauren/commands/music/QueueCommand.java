package com.yuhtin.lauren.commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.menu.Paginator;
import com.yuhtin.lauren.core.music.AudioInfo;
import com.yuhtin.lauren.core.music.TrackManager;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import com.yuhtin.lauren.utils.helper.TrackUtils;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@CommandHandler(
        name = "playlist",
        type = CommandHandler.CommandType.MUSIC,
        description = "Ver as músicas que eu ainda vou tocar",
        alias = {"queue", "pl", "fila"}
)
public class QueueCommand extends Command {

    public static final Paginator.Builder builder = new Paginator.Builder()
            .setColumns(1)
            .setFinalAction(message -> message.clearReactions().queue())
            .setItemsPerPage(10)
            .waitOnSinglePage(false)
            .useNumberedItems(true)
            .showPageNumbers(true)
            .wrapPageEnds(true)
            .setTimeout(1, TimeUnit.MINUTES);

    @Override
    protected void execute(CommandEvent event) {
        TrackManager trackManager = TrackManager.get();
        if (trackManager.getQueuedTracks().isEmpty()) {
            event.getChannel().sendMessage("\uD83D\uDCCC Eita, não tem nenhum batidão pra tocar, adiciona uns ai <3").queue();
            return;
        }

        int page = 1;
        try {
            page = Integer.parseInt(event.getArgs());
        } catch (NumberFormatException ignore) {
        }

        Set<AudioInfo> queue = trackManager.getQueuedTracks();
        String[] songs = new String[queue.size()];
        long totalTime = 0;

        int i = 0;
        for (AudioInfo audioInfo : queue) {
            totalTime += audioInfo.getTrack().getInfo().length;
            songs[i] = audioInfo.toString();

            ++i;
        }

        String timeInLetter = TrackUtils.get().getTimeStamp(totalTime);
        builder.setText((number, number2) -> {
            StringBuilder stringBuilder = new StringBuilder();
            if (trackManager.player.getPlayingTrack() != null) {

                stringBuilder.append(trackManager.player.isPaused() ? "\u23F8" : "\u25B6")
                        .append(" **")
                        .append(trackManager.player.getPlayingTrack().getInfo().title)
                        .append("**")
                        .append(" - ")
                        .append("`")
                        .append(TrackUtils.get().getTimeStamp(trackManager.player.getPlayingTrack().getPosition()))
                        .append(" / ")
                        .append(TrackUtils.get().getTimeStamp(trackManager.player.getPlayingTrack().getInfo().length))
                        .append("`")
                        .append("\n");

            }

            return stringBuilder.append("\uD83D\uDCBF Informações da Fila | ")
                    .append(queue.size())
                    .append(" músicas | `")
                    .append(timeInLetter)
                    .append("`")
                    .toString();
        }).setItems(songs)
                .setUsers(event.getAuthor())
                .setColor(event.getSelfMember().getColor());

        builder.build().paginate(event.getChannel(), page);
    }
}
