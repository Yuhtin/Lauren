package com.yuhtin.lauren.commands.impl.music;

import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandHandler;
import com.yuhtin.lauren.core.music.TrackManager;
import com.yuhtin.lauren.utils.helper.TimeUtils;
import com.yuhtin.lauren.utils.helper.TrackUtils;
import com.yuhtin.lauren.utils.helper.UserUtil;
import lombok.val;
import lombok.var;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

import java.util.concurrent.TimeUnit;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@CommandHandler(
        name = "skipto",
        type = CommandHandler.CommandType.MUSIC,
        description = "Pular para uma certa minutagem na música",
        args = {"<tempo>-Exemplo de tempos válidos: **35** ou **15:13**"}
)
public class JumpToTimeCommand implements Command {

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) {
        if (event.getGuild() == null
                || event.getMember() == null
                || TrackUtils.get().isIdle(event.getGuild(), hook)
                || !UserUtil.isDJ(event.getMember(), hook)) return;

        val args = event.getOption("tempo").getAsString();
        long millis;
        if (!args.contains(":")) {
            long secondsInMillis = parseInt(args, TimeUnit.SECONDS, hook);
            if (secondsInMillis == -1) return;

            millis = secondsInMillis;
        } else {
            val split = args.split(":");
            val secondsInMillis = parseInt(split[split.length - 1], TimeUnit.SECONDS, hook);
            if (secondsInMillis == -1) return;

            var minutesInMillis = 0L;
            if (split.length > 1) {
                minutesInMillis = parseInt(split[split.length - 2], TimeUnit.MINUTES, hook);
                if (minutesInMillis == -1) return;
            }

            var hoursInMillis = 0L;
            if (split.length > 2) {
                hoursInMillis = parseInt(split[split.length - 3], TimeUnit.HOURS, hook);
                if (hoursInMillis == -1) return;
            }

            millis = hoursInMillis + minutesInMillis + secondsInMillis;
        }

        val track = TrackManager.of(event.getGuild()).getTrackInfo().getTrack();
        long duration = track.getDuration();
        if (millis > duration) {
            hook.sendMessage("<:pensando:781761324547309594> A música é menor que o tempo inserido, se quiser skipar use `$skip`").queue();
            return;
        }

        track.setPosition(millis);
        hook.sendMessage("<:felizpakas:742373250037710918> Pulando a música para `" + TimeUtils.formatTime(millis) + "`").queue();
    }

    private long parseInt(String arg, TimeUnit timeUnit, InteractionHook hook) {
        try {
            var time = Integer.parseInt(arg);

            if (time < 0) time = 0;
            else if (timeUnit == TimeUnit.SECONDS && time > 60) time = 60;
            else if (timeUnit == TimeUnit.MINUTES && time > 60) time = 60;
            else if (timeUnit == TimeUnit.HOURS && time > 24) time = 24;

            return timeUnit.toMillis(time);
        } catch (Exception exception) {
            hook.sendMessage("<:pensando:781761324547309594> Você digitou um tempo inválido").queue();
            return -1;
        }

    }
}
