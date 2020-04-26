package commands;

import application.Lauren;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;

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

                .addField("<a:infinito:703187274912759899> Uptime", "`" + format(Lauren.startTime) + "`", true)
                .addField("💥 Servidores", "`" + event.getJDA().getGuilds().size() + " " + (event.getJDA().getGuilds().size() > 1 ? "servidores" : "servidor") + "`", true)
                .addField("🏓 Ping da API", "`" + event.getJDA().getGatewayPing() + "ms`", true)

                .addField("", "", true)
                .addField("", "", true)
                .addField("", "", true)

                .setFooter(authorBot, bot.getAvatarUrl())
                .setColor(event.getMember().getColor())
                .setThumbnail(bot.getAvatarUrl())
                .setTimestamp(Instant.now());

        event.getChannel().sendMessage(builder.build()).queue();
    }

    private String format(long time) {
        time = System.currentTimeMillis() - time;
        String format = "";
        long hours = TimeUnit.MILLISECONDS.toHours(time);
        long hoursInMillis = TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(time - hoursInMillis);
        long minutesInMillis = TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(time - (hoursInMillis + minutesInMillis));
        int days = (int) (time / (1000 * 60 * 60 * 24));
        if (hours > 0)
            if (days > 0) {
                time = time - TimeUnit.DAYS.toMillis(days);
                hours = TimeUnit.MILLISECONDS.toHours(time - minutesInMillis);
                format = days + " dias, " + hours + (hours > 1 ? " horas" : " hora");
                return format;
            } else {
                format = hours + (hours > 1 ? " horas" : " hora");
            }
        if (minutes > 0) {
            if ((seconds > 0) && (hours > 0))
                format += ", ";
            else if (hours > 0)
                format += " e ";
            format += minutes + (minutes > 1 ? " minutos" : " minuto");
        }
        if (seconds > 0) {
            if ((hours > 0) || (minutes > 0))
                format += " e ";
            format += seconds + (seconds > 1 ? " segundos" : " segundo");
        }
        if (format.equals("")) {
            long rest = time / 100;
            if (rest == 0)
                rest = 1;
            format = "0." + rest + " segundo";
        }
        if (days > 0) {
            format = days + " dias";
        }
        return format;
    }
}
