package com.yuhtin.lauren.commands.impl.music;

import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.core.music.TrackManager;
import com.yuhtin.lauren.commands.CommandHandler;
import com.yuhtin.lauren.utils.helper.TrackUtils;
import com.yuhtin.lauren.utils.helper.UserUtil;
import lombok.val;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

@CommandHandler(
        name = "bass",
        type = CommandHandler.CommandType.MUSIC,
        description = "Mudar os graves e agudos do meu batidão",
        args = {"<boost>-Opções válidas low, high, boost ou normal"}
)
public class BassBoostCommand implements Command {

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) {
        if (TrackUtils.get().isIdle(event.getTextChannel())) return;
        if (!UserUtil.isDJ(event.getMember(), event.getTextChannel(), true)) return;

        val boost = event.getOption("boost").getAsString();
        TrackManager trackManager = TrackManager.of(event.getGuild());
        switch (boost) {

            case "low":

                trackManager.eqLowBass(-.35f);
                event.getChannel().sendMessage(":loud_sound: Equalizando o baixo da música!").queue();
                break;

            case "high":

                trackManager.eqHighBass(.083f);
                event.getChannel().sendMessage(":loud_sound: Equalizando o grave da música!").queue();
                break;


            case "boost":

                trackManager.bassBoost();
                event.getChannel().sendMessage(":loud_sound: Equalizando tudão, cuidado rapaziada!").queue();
                break;


            case "normal":

                trackManager.eqHighBass(0);
                trackManager.eqLowBass(0);
                event.getChannel().sendMessage(":loud_sound: Tirando equalização").queue();
                break;


            default:
                event.getChannel()
                        .sendMessage(":grey_question: Não encontrei essa equalização, equalizações válidas: `low, high, boost ou normal`")
                        .queue();
        }
    }

}
