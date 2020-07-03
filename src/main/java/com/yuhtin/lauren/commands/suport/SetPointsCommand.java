package com.yuhtin.lauren.commands.suport;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.core.player.PlayerData;
import com.yuhtin.lauren.core.player.controller.PlayerDataController;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import com.yuhtin.lauren.utils.helper.Utilities;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;

import java.util.concurrent.TimeUnit;

@CommandHandler(
        name = "setarpontos",
        type = CommandHandler.CommandType.SUPORT,
        description = "Setar os pontos de ranked para um jogador",
        alias = {"setpoints"})
public class SetPointsCommand extends Command {

    public SetPointsCommand() {
        this.name = "setpoints";
        this.aliases = new String[]{"setarpontos"};
    }

    @Override
    protected void execute(CommandEvent event) {
        if (!Utilities.isPermission(event.getMember(), event.getChannel(), Permission.ADMINISTRATOR))
            return;

        if (event.getMessage().getMentionedMembers().size() < 1) {
            event.getChannel().sendMessage("Ops, você precisa mencionar um jogador para receber os pontos")
                    .queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
            return;
        }

        Member member = event.getMessage().getMentionedMembers().get(0);
        String[] arguments = event.getMessage().getContentRaw().split(" ");

        if (arguments.length < 3) {
            event.getChannel().sendMessage("Utilize desta forma: " + arguments[0] + " @Usuario <Ball ou Ludo> <quantidade>")
                    .queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
            return;
        }

        int xp = Integer.parseInt(arguments[3]);
        PlayerData data = PlayerDataController.get(member.getIdLong());

        if (arguments[2].equalsIgnoreCase("Ludo")) data.ludoPoints = xp;
        else if (arguments[2].equalsIgnoreCase("Ball")) data.poolPoints = xp;

        else {
            event.getChannel().sendMessage("Este jogo é invalido. Jogos válidos:")
                    .queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
            event.getChannel().sendMessage("Ball ou Ludo")
                    .queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
            return;
        }

        data.updateRank().save();
        event.getChannel().sendMessage("Você setou **" + xp + "** pontos no jogo " + arguments[2] + " para o jogador " + member.getUser().getName()).queue();
    }
}
