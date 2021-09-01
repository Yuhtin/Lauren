package com.yuhtin.lauren.commands.impl.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.yuhtin.lauren.core.music.TrackManager;
import com.yuhtin.lauren.commands.CommandHandler;
import com.yuhtin.lauren.utils.helper.TimeUtils;
import com.yuhtin.lauren.utils.helper.TrackUtils;
import com.yuhtin.lauren.utils.helper.UserUtil;
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
        if (!UserUtil.INSTANCE.isDJ(event.getMember(), event.getChannel(), true)) return;

        String args = event.getArgs();
        if (args.equalsIgnoreCase("")) {

            event.getChannel().sendMessage(
                    "<:pensando:781761324547309594> Você precisa colocar um tempo, exemplo: `35` ou `15:13`"
            ).queue();
            return;

        }

        long millis;

        if (!args.contains(":")) {

            long secondsInMillis = parseInt(args, TimeUnit.SECONDS, event.getTextChannel());
            if (secondsInMillis == -1) return;

            millis = secondsInMillis;

        } else {

            String[] split = args.split(":");

            long secondsInMillis = parseInt(split[split.length - 1], TimeUnit.SECONDS, event.getTextChannel());
            if (secondsInMillis == -1) return;

            long minutesInMillis = 0;
            if (split.length > 1) {

                minutesInMillis = parseInt(split[split.length - 2], TimeUnit.MINUTES, event.getTextChannel());
                if (minutesInMillis == -1) return;

            }

            long hoursInMillis = 0;
            if (split.length > 2) {

                hoursInMillis = parseInt(split[split.length - 3], TimeUnit.HOURS, event.getTextChannel());
                if (hoursInMillis == -1) return;

            }

            millis = hoursInMillis + minutesInMillis + secondsInMillis;

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
            else if (timeUnit == TimeUnit.MINUTES && time > 60) time = 60;
            else if (timeUnit == TimeUnit.HOURS && time > 24) time = 24;

            return timeUnit.toMillis(time);

        } catch (Exception exception) {

            channel.sendMessage(
                    "<:pensando:781761324547309594> Você digitou um tempo inválido"
            ).queue();

            return -1;
        }

    }
}
