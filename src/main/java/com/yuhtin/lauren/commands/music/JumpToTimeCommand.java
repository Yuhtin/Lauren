package com.yuhtin.lauren.commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.yuhtin.lauren.core.music.TrackManager;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import com.yuhtin.lauren.utils.helper.MathUtils;
import com.yuhtin.lauren.utils.helper.TimeUtils;
import com.yuhtin.lauren.utils.helper.TrackUtils;
import com.yuhtin.lauren.utils.helper.Utilities;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.concurrent.TimeUnit;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@CommandHandler(
        name = "skipto",
        type = CommandHandler.CommandType.MUSIC,
        description = "Pular para uma certa minutagem na música",
        alias = {"jumpto", "pularpara", "avancar", "time", "tempo"}
)
public class JumpToTimeCommand extends Command {

    @Override
    protected void execute(CommandEvent event) {
        if (TrackUtils.get().isIdle(event.getTextChannel())) return;
        if (!Utilities.INSTANCE.isDJ(event.getMember(), event.getChannel(), true)) return;

        String args = event.getArgs();
        if (args.equalsIgnoreCase("")) {

            event.getChannel().sendMessage(
                    "<:pensando:781761324547309594> Você precisa colocar um tempo, exemplo: `35` ou `15:13`"
            ).queue();
            return;

        }

        long millis = 0;

        if (!args.contains(":")) {

            long secondsInMillis = parseInt(args, TimeUnit.SECONDS, event.getTextChannel());
            if (secondsInMillis == -1) return;

            millis = secondsInMillis;

        } else {

            String[] split = args.split(":");
            if (split.length == 2) {

                long minutesInMillis = parseInt(split[0], TimeUnit.MINUTES, event.getTextChannel());
                if (minutesInMillis == -1) return;

                long secondsInMillis = parseInt(split[1], TimeUnit.SECONDS, event.getTextChannel());
                if (secondsInMillis == -1) return;

                millis = minutesInMillis + secondsInMillis;

            }

        }

        AudioTrack track = TrackManager.of(event.getGuild()).getTrackInfo().getTrack();

        long duration = track.getDuration();
        if (millis > duration) {

            event.getChannel().sendMessage(
                    "<:pensando:781761324547309594> A música é menor que o tempo inserido, se quiser skipar use `$skip`"
            ).queue();

        }

        track.setPosition(millis);

        event.getChannel().sendMessage(
                "<:felizpakas:742373250037710918> Pulando a música para `" + TimeUtils.formatTime(millis) + "`"
        ).queue();

    }

    private long parseInt(String arg, TimeUnit timeUnit, MessageChannel channel) {

        try {

            int time = Integer.parseInt(arg);

            if (time < 0) time = 0;
            else if (timeUnit == TimeUnit.SECONDS && time > 60) time = 60;
            else if (timeUnit == TimeUnit.MINUTES && time > 30) time = 30;

            return timeUnit.toMillis(time);

        } catch (Exception exception) {

            channel.sendMessage(
                    "<:pensando:781761324547309594> Você digitou um tempo inválido"
            ).queue();

            return -1;
        }

    }
}
