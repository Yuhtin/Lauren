package com.yuhtin.lauren.commands.impl.help;

import com.google.inject.Inject;
import com.yuhtin.lauren.core.music.TrackManager;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.core.statistics.StatsController;
import com.yuhtin.lauren.commands.CommandData;
import com.yuhtin.lauren.startup.Startup;
import com.yuhtin.lauren.utils.helper.SystemStatsUtils;
import com.yuhtin.lauren.utils.helper.TimeUtils;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

@CommandData(
        name = "info",
        type = CommandData.CommandType.HELP,
        description = "Veja um pouco mais sobre mim",
        alias = {"binfo"}
)
public class InfoCommand implements CommandExecutor {

    @Inject private PlayerController playerController;
    @Inject private StatsController statsController;

    @SneakyThrows
    @Override
    public void execute(CommandEvent event) {
        SelfUser bot = event.getJDA().getSelfUser();
        OffsetDateTime timeCreated = bot.getTimeCreated();

        User user = event.getJDA().getUserById(272879983326658570L);
        String authorBot = user == null ? bot.getName() + "#" + bot.getDiscriminator() : user.getName() + "#" + user.getDiscriminator();

        TrackManager trackManager = TrackManager.of(event.getGuild());
        String cacheMessage = this.playerController.totalUsers()
                + " jogadores, "
                + trackManager.getMusicManager().scheduler.queue.size()
                + " músicas e "
                + this.statsController.getStats().size()
                + " estatísticas";


        EmbedBuilder builder = new EmbedBuilder()
                .setAuthor("Informações sobre a bot mais linda do mundo", null, bot.getAvatarUrl())

                .addField("📆 Criado em", "`" + timeCreated.getDayOfMonth() + " de " + timeCreated.getMonth().getDisplayName(TextStyle.SHORT, Locale.US) + ", "
                        + timeCreated.getYear() + " às " + timeCreated.getHour() + ":" + timeCreated.getMinute() + "`", true)
                .addField("<a:feliz_2:726220815749611603> Versão atual", "`v" + Startup.getLauren().getVersion() + "`", true)
                .addField("🙍‍♂️ Dono", "`" + authorBot + "`", true)

                .addField("<a:infinito:703187274912759899> Uptime",
                        "`" + TimeUtils.formatTime(System.currentTimeMillis() - Startup.getLauren().getBotStartTime()) + "`",
                        true)

                .addField("💥 Cache", "`" + cacheMessage + "`", true)
                .addField("🏓 Ping da API", "`" + event.getJDA().getGatewayPing() + "ms`", true)

                .addField("\uD83D\uDD8A Prefixo", "Padrão: `$`", true)
                .addField("<:java:723609384428503071> Versão Java", "`v" + System.getProperty("java.version") + "`", true)
                .addField("<:discord:723587554422816889> Versão JDA", "`v4.2.0_186`", true)

                .addField("⚙️ Núcleos", "`" + Runtime.getRuntime().availableProcessors() + " cores`", true)
                .addField("\uD83D\uDEE2 Banco de Dados", "`MySQL`", true)
                .addField("\uD83C\uDF9E RAM", "`"
                        + SystemStatsUtils.usedMemory() + "/"
                        + SystemStatsUtils.totalMemory() + "`", true)

                .setFooter("Mais informações em $ping", event.getAuthor().getAvatarUrl())
                .setColor(event.getMember().getColor())
                .setThumbnail(bot.getAvatarUrl())
                .setTimestamp(Instant.now());

        event.getChannel().sendMessageEmbeds(builder.build()).queue();
    }
}
