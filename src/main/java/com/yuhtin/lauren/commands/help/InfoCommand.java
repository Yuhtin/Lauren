package com.yuhtin.lauren.commands.help;

import com.yuhtin.lauren.application.Lauren;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import lombok.SneakyThrows;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import com.yuhtin.lauren.utils.helper.MathUtils;

import java.lang.management.OperatingSystemMXBean;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

@CommandHandler(name = "info", type = CommandHandler.CommandType.HELP, description = "Veja um pouco mais sobre mim")
public class InfoCommand extends Command {
    public InfoCommand() {
        this.name = "botinfo";
        this.aliases = new String[]{"info", "binfo"};
    }

    @SneakyThrows
    @Override
    protected void execute(CommandEvent event) {
        SelfUser bot = event.getJDA().getSelfUser();
        User user = event.getJDA().getUserById(272879983326658570L);
        String authorBot = user == null ? bot.getName() + "#" + bot.getDiscriminator() : user.getName() + "#" + user.getDiscriminator();
        OffsetDateTime before = bot.getTimeCreated();
        long maxMemory = Runtime.getRuntime().maxMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();
        long usedMemory = maxMemory - freeMemory;

        EmbedBuilder builder = new EmbedBuilder()
                .setAuthor("\uD83D\uDDC2 Informações sobre a bot mais linda do mundo", "https://google.com", bot.getAvatarUrl())

                .addField("📆 Criado em", "`" + before.getDayOfMonth() + " de " + before.getMonth().getDisplayName(TextStyle.SHORT, Locale.US) + ", "
                        + before.getYear() + " às " + before.getHour() + ":" + before.getMinute() + "`", true)
                .addField("🌌 Meu ID", "`" + bot.getId() + "`", true)
                .addField("🙍‍♂️ Dono", "`" + authorBot + "`", true)

                .addField("<a:infinito:703187274912759899> Uptime", "`" + MathUtils.format(System.currentTimeMillis() - Lauren.startTime) + "`", true)
                .addField("💥 Servidores", "`Sou exclusiva deste servidor :d`", true)
                .addField("🏓 Ping da API", "`" + event.getJDA().getGatewayPing() + "ms`", true)

                .addField("\uD83D\uDD8A Prefixos", "Padrão: `$`", true)
                .addField("<:java:723609384428503071> Versão Java", "`" + System.getProperty("java.version") + "`", true)
                .addField("<:discord:723587554422816889> Versão Discord API", "`v4.1.1_137`", true)

                .addField("⚙️ Núcleos", "`" + Runtime.getRuntime().availableProcessors() + " cores`", true)
                .addField("\uD83D\uDEE2 Banco de Dados", "`" + Lauren.config.databaseType + "`", true)
                .addField("\uD83C\uDF9E RAM", "`" + MathUtils.bytesToLegibleValue(usedMemory) + "/" + MathUtils.bytesToLegibleValue(maxMemory) + "`", true)

                .setFooter("Mais informações em $ping", event.getAuthor().getAvatarUrl())
                .setColor(event.getMember().getColor())
                .setThumbnail(bot.getAvatarUrl())
                .setTimestamp(Instant.now());

        event.getChannel().sendMessage(builder.build()).queue();
    }
}
