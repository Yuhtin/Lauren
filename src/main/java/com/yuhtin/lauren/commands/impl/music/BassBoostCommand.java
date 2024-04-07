package com.yuhtin.lauren.commands.impl.music;

import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import lombok.val;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

@CommandInfo(
        name = "bassboost",
        type = CommandType.MUSIC,
        description = "Mudar os graves e agudos do meu batidão",
        args = {"<boost>-Opções válidas low, high, boost ou normal"}
)
public class BassBoostCommand implements Command {

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) {
        if (event.getGuild() == null
                || event.getMember() == null
                || !UserUtil.isDJ(event.getMember(), hook)) return;

        val boost = event.getOption("boost").getAsString();
        val trackManager = TrackManager.getByGuild(event.getGuild());
        switch (boost) {

            case "low":

                trackManager.eqLowBass(-.35f);
                hook.sendMessage(":loud_sound: Equalizando o baixo da música!").queue();
                break;

            case "high":

                trackManager.eqHighBass(.083f);
                hook.sendMessage(":loud_sound: Equalizando o grave da música!").queue();
                break;


            case "boost":

                trackManager.bassBoost();
                hook.sendMessage(":loud_sound: Equalizando tudão, cuidado rapaziada!").queue();
                break;


            case "normal":

                trackManager.eqHighBass(0);
                trackManager.eqLowBass(0);
                hook.sendMessage(":loud_sound: Tirando equalização").queue();
                break;


            default:
                hook.sendMessage(":grey_question: Não encontrei essa equalização, equalizações válidas: `low, high, boost ou normal`")
                        .queue();
        }
    }

}
