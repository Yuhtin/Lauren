package com.yuhtin.lauren.commands.impl.music;

import com.yuhtin.lauren.Startup;
import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import com.yuhtin.lauren.commands.CommandType;
import com.yuhtin.lauren.module.Module;
import com.yuhtin.lauren.module.impl.music.MusicModule;
import com.yuhtin.lauren.module.impl.player.module.PlayerModule;
import com.yuhtin.lauren.util.EmbedUtil;
import com.yuhtin.lauren.util.MusicUtil;
import com.yuhtin.lauren.util.Paginator;
import lombok.Getter;
import lombok.val;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

import java.util.concurrent.TimeUnit;

@CommandInfo(
        name = "queue",
        type = CommandType.MUSIC,
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

        PlayerModule playerModule = Module.instance(PlayerModule.class);
        if (playerModule == null) return;

        MusicModule musicModule = Module.instance(MusicModule.class);
        if (musicModule == null) return;

        musicModule.getByGuildId(event.getGuild()).queue(trackManager -> {
            if (MusicUtil.isIdle(trackManager, hook)) return;
            if (trackManager.getPlaylist().isEmpty()) {
                hook.sendMessageEmbeds(EmbedUtil.create("Eita não tem nenhum batidão tocando, adiciona uns ai <3")).queue();
                return;
            }

            val pageOption = event.getOption("pagina");
            val page = pageOption == null ? 1: (int) pageOption.getAsDouble();

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
                    .setUsers(event.getUser())
                    .setColor(event.getMember().getColor());

            BUILDER.build().paginate(hook, page);
        });
    }
}
