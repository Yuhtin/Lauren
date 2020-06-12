package commands.scrim;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import data.PlayerData;
import data.controller.PlayerDataController;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import utils.helper.Utilities;

import java.util.concurrent.TimeUnit;

public class SetPointsCommand extends Command {
    public SetPointsCommand() {
        this.name = "setpoints";
        this.aliases = new String[]{"setarpontos"};
        this.help = "Setar os pontos de um certo jogo a uma uma pessoa";
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
        PlayerData data = PlayerDataController.get(member);

        if (arguments[2].equalsIgnoreCase("Ludo")) {
            data.ludoPoints = xp;
        } else if (arguments[2].equalsIgnoreCase("Ball"))
            data.poolPoints = xp;
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
