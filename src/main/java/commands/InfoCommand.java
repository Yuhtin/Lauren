package commands;

import application.Lauren;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import utils.helper.MathUtils;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class InfoCommand extends Command {
    public InfoCommand() {
        this.name = "botinfo";
        this.aliases = new String[]{"info", "binfo"};
        this.help = "Informações do bot";
    }

    @SneakyThrows
    @Override
    protected void execute(CommandEvent event) {
        SelfUser bot = event.getJDA().getSelfUser();
        User user = event.getJDA().getUserById(272879983326658570L);
        String authorBot = user == null ? bot.getName() + "#" + bot.getDiscriminator() : user.getName() + "#" + user.getDiscriminator();
        OffsetDateTime before = bot.getTimeCreated();

        EmbedBuilder builder = new EmbedBuilder()
                .setAuthor("Informações sobre o Bot", "https://google.com", bot.getAvatarUrl())

                .addField("📆 Criado em", "`" + before.getDayOfMonth() + " de " + before.getMonth().getDisplayName(TextStyle.SHORT, Locale.US) + ", "
                        + before.getYear() + " às " + before.getHour() + ":" + before.getMinute() + "`", true)
                .addField("🌌 Meu ID", "`" + bot.getId() + "`", true)
                .addField("🙍‍♂️ Dono", "`" + authorBot + "`", true)

                .addField("<a:infinito:703187274912759899> Uptime", "`" + MathUtils.format(Lauren.startTime) + "`", true)
                .addField("💥 Servidores", "`" + event.getJDA().getGuilds().size() + " " + (event.getJDA().getGuilds().size() > 1 ? "servidores" : "servidor") + "`", true)
                .addField("🏓 Ping da API", "`" + event.getJDA().getGatewayPing() + "ms`", true)

                .addField("\uD83D\uDC52 Andrey é gay", "`é mesmo`", true)
                .addField("", "", true)
                .addField("", "", true)

                .setFooter(authorBot, bot.getAvatarUrl())
                .setColor(event.getMember().getColor())
                .setThumbnail(bot.getAvatarUrl())
                .setTimestamp(Instant.now());

        event.getChannel().sendMessage(builder.build()).queue();
    }
}
