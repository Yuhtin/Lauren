package commands.scrim;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import models.cache.PlayerDataCache;
import models.data.PlayerData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import utils.helper.Utilities;

import java.time.Instant;

public class PlayerInfoCommand extends Command {
    public PlayerInfoCommand() {
        this.name = "playerinfo";
        this.aliases = new String[]{"pinfo"};
        this.help = "Informações sobre um jogador.";
    }

    @Override
    protected void execute(CommandEvent event) {
        Member target = event.getMessage().getMentionedMembers().size() < 1 ? event.getMember() : event.getMessage().getMentionedMembers().get(0);
        PlayerData controller = PlayerDataCache.get(target);

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(target.getColor());
        embedBuilder.setAuthor("Informações do jogador " + target.getNickname(), "https://google.com", target.getUser().getAvatarUrl());
        embedBuilder.setThumbnail(target.getUser().getAvatarUrl());

        embedBuilder.addField("⚗️ Experiência", "`Nível " + controller.level + " (" + Utilities.format(controller.experience) + " XP)`", false);
        embedBuilder.addField("🧶 Cargos", "`" + Utilities.rolesToString(target.getRoles()) + "`", false);
        embedBuilder.addField("\uD83D\uDC7E Partidas totais", "`" + (controller.ludoMatches + controller.poolMatches) + "`", false);
        embedBuilder.addField("\uD83D\uDCB0 Dinheiro", "`$" + (Utilities.format(controller.money)) + "`", false);
        embedBuilder.addField("\uD83C\uDFB1 8BallPool",
                "  \uD83D\uDD25 Partidas: " + controller.poolMatches + " \n" +
                "  \uD83E\uDD47 Vitórias: " + controller.poolWins + " \n\n" +
                "  \uD83C\uDFC6 Patente: " + controller.poolRank + "", true);
        embedBuilder.addField("👑 LudoKing",
                "  \uD83D\uDD25 Partidas: " + controller.ludoMatches + " \n" +
                "  \uD83E\uDD47 Vitórias: " + controller.ludoWins + " \n\n" +
                "  \uD83C\uDFC6 Patente: " + controller.ludoRank + "", true);

        embedBuilder.setFooter("Comando usado por " + event.getMember().getNickname(), event.getMember().getUser().getAvatarUrl());
        embedBuilder.setTimestamp(Instant.now());
        event.getChannel().sendMessage(embedBuilder.build()).queue();
    }
}
