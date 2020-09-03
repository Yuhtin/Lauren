package com.yuhtin.lauren.commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.core.music.AudioInfo;
import com.yuhtin.lauren.core.music.TrackManager;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import com.yuhtin.lauren.utils.helper.TrackUtils;
import com.yuhtin.lauren.utils.helper.Utilities;

@CommandHandler(
        name = "pular",
        type = CommandHandler.CommandType.MUSIC,
        description = "Iniciar uma votação para pular a música atual",
        alias = {"votar", "skip"}
)
public class SkipCommand extends Command {

    @Override
    protected void execute(CommandEvent event) {
        if (TrackUtils.get().isIdle(event.getTextChannel())) return;
        if (TrackUtils.get().isCurrentDj(event.getMember())) {
            TrackUtils.get().forceSkipTrack();
            event.getChannel().sendMessage("\u23e9 Pulei a música pra você <3").queue();
            return;
        }

        AudioInfo info = TrackManager.get().getTrackInfo();
        if (info.hasVoted(event.getAuthor())) {
            event.getChannel().sendMessage("\uD83D\uDC6E\uD83C\uDFFD\u200D♀️ Ei você já votou pra pular essa música ;-;").queue();
            return;
        }

        info.addSkip(event.getAuthor());
        if (info.getSkips() >= TrackManager.get().audio.getMembers().size() - 2) {
            TrackUtils.get().forceSkipTrack();
            event.getChannel().sendMessage("\uD83E\uDDF6 Amo quando todos concordam entre si, pulando a música").queue();
            return;
        }

        String name = event.getMember().getNickname() == null ?
                Utilities.INSTANCE.getFullName(event.getAuthor())
                : event.getMember().getNickname();

        event.getChannel()
                .sendMessage("\uD83E\uDDEC **" + name + "** votou para pular a música **(" + info.getSkips()
                        + "/" + (TrackManager.get().audio.getMembers().size() - 2) + ")**")
                .queue();
    }
}
