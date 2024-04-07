package com.yuhtin.lauren.commands.impl.help;

import com.google.inject.Inject;
import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.core.statistics.StatsController;
import com.yuhtin.lauren.startup.Startup;
import com.yuhtin.lauren.util.SystemUtil;
import com.yuhtin.lauren.util.TimeUtils;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

import java.time.Instant;
import java.time.format.TextStyle;
import java.util.Locale;

@CommandInfo(
        name = "botinfo",
        type = CommandType.HELP,
        description = "Veja um pouco mais sobre mim"
)
public class InfoCommand implements Command {

    @Inject private PlayerController playerController;
    @Inject private StatsController statsController;

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) throws Exception {
        val bot = event.getJDA().getSelfUser();
        val timeCreated = bot.getTimeCreated();

        int musicsQueue = 0;
        for (val trackManager : TrackManager.getCacheByGuildId().values()) {
           musicsQueue += trackManager.getQueuedTracks().size();
        }

        val cacheMessage = playerController.totalUsers()
                + " jogadores, "
                + musicsQueue
                + " músicas e "
                + statsController.getStats().size()
                + " estatísticas";


        val builder = new EmbedBuilder()
                .setAuthor("Informações sobre a bot mais linda do mundo", null, bot.getAvatarUrl())

                .addField("📆 Criado em", "`" + timeCreated.getDayOfMonth() + " de " + timeCreated.getMonth().getDisplayName(TextStyle.SHORT, Locale.US) + ", "
                        + timeCreated.getYear() + " às " + timeCreated.getHour() + ":" + timeCreated.getMinute() + "`", true)
                .addField("<a:feliz_2:726220815749611603> Versão atual", "`v" + Startup.getLauren().getVersion() + "`", true)
                .addField("🙍‍♂️ Dono", "`Yuhtin#9147`", true)

                .addField("<a:infinito:703187274912759899> Uptime",
                        "`" + TimeUtils.formatTime(System.currentTimeMillis() - Startup.getLauren().getBotStartTime()) + "`",
                        true)

                .addField("💥 Cache", "`" + cacheMessage + "`", true)
                .addField("🏓 Ping da API", "`" + event.getJDA().getGatewayPing() + "ms`", true)

                .addField("\uD83D\uDD8A Prefixo", "Padrão: `/`", true)
                .addField("<:java:723609384428503071> Versão Java", "`v" + System.getProperty("java.version") + "`", true)
                .addField("<:discord:723587554422816889> Versão JDA", "`v4.2.0_186`", true)

                .addField("⚙️ Núcleos", "`" + Runtime.getRuntime().availableProcessors() + " cores`", true)
                .addField("\uD83D\uDEE2 Banco de Dados", "`MongoDB`", true)
                .addField("\uD83C\uDF9E RAM", "`"
                        + SystemUtil.usedMemory() + "/"
                        + SystemUtil.totalMemory() + "`", true)

                .setFooter("Mais informações em /ping", event.getUser().getAvatarUrl())
                .setColor(event.getMember().getColor())
                .setThumbnail(bot.getAvatarUrl())
                .setTimestamp(Instant.now());

        hook.setEphemeral(true).sendMessageEmbeds(builder.build()).queue();
    }

}
