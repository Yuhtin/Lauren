package commands.scrim;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import models.annotations.CommandHandler;
import models.cache.PlayerDataCache;
import models.data.PlayerData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import utils.helper.Utilities;

import java.time.Instant;

@CommandHandler(name = "perfil", type = CommandHandler.CommandType.SCRIM, description = "Visualizar o perfil de outro usuário")
public class PlayerInfoCommand extends Command {

    public PlayerInfoCommand() {
        this.name = "perfil";
        this.aliases = new String[]{"pinfo", "jogador", "playerinfo", "player"};
    }

    @Override
    protected void execute(CommandEvent event) {
        Member target = event.getMessage().getMentionedMembers().size() < 1 ? event.getMember() : event.getMessage().getMentionedMembers().get(0);
        PlayerData controller = PlayerDataCache.get(target);

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(target.getColor())
                .setAuthor("Informações do jogador " + target.getNickname(), "https://google.com", target.getUser().getAvatarUrl())
                .setThumbnail(controller.ludoRank.position > controller.poolRank.position ? controller.ludoRank.url : controller.poolRank.url)
                .addField("⚗️ Experiência", "`Nível " + controller.level + " (" + Utilities.format(controller.experience) + " XP)`", false)
                .addField("🧶 Cargos", "`" + Utilities.rolesToString(target.getRoles()) + "`", false)
                .addField("\uD83D\uDCB0 Dinheiro", "`$" + (Utilities.format(controller.money)) + "`", false)
                .addField("\uD83D\uDC7E Partidas totais", "`" + (controller.ludoMatches + controller.poolMatches) + "`", false)
                .addField("\uD83C\uDFB1 8BallPool",
                        "  \uD83D\uDD25 Partidas: " + controller.poolMatches + " \n" +
                                "  \uD83E\uDD47 Vitórias: " + controller.poolWins + " \n\n" +
                                "  \uD83C\uDFC6 Patente: " + controller.poolRank + "", true)
                .addField("👑 LudoKing",
                        "  \uD83D\uDD25 Partidas: " + controller.ludoMatches + " \n" +
                                "  \uD83E\uDD47 Vitórias: " + controller.ludoWins + " \n\n" +
                                "  \uD83C\uDFC6 Patente: " + controller.ludoRank + "", true)
                .setFooter("Comando usado por " + event.getMember().getNickname(), event.getMember().getUser().getAvatarUrl())
                .setTimestamp(Instant.now());

        event.getChannel().sendMessage(embedBuilder.build()).queue();
    }
}
