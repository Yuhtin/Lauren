package com.yuhtin.lauren.commands.impl.help;

import com.google.inject.Inject;
import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import com.yuhtin.lauren.service.LocaleManager;
import com.yuhtin.lauren.startup.Startup;
import com.yuhtin.lauren.utils.SystemStatsUtils;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

import java.time.Instant;

@CommandInfo(
        name = "host",
        type = CommandInfo.CommandType.HELP,
        description = "Verificar as informações da minha hospedagem"
)
public class PingCommand implements Command {

    @Inject private LocaleManager localeManager;

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) throws Exception {
        if (event.getMember() == null) return;

        long actual = System.currentTimeMillis();
        hook.setEphemeral(true).sendMessage("Carregando...").queue(message -> {
            MessageEmbed embed = createEmbed(
                    message.getTimeCreated().toInstant().toEpochMilli() - actual,
                    event.getMember(),
                    event.getJDA()
            );

            message.editMessageEmbeds(embed).queue();
        });
    }

    private MessageEmbed createEmbed(long toEpochMilli, Member member, JDA jda) {
        val shardManager = Startup.getLauren().getBot().getShardManager();
        val shardMessage = (shardManager == null ? "1" : shardManager.getShardsTotal())
                + " shards, "
                + (shardManager == null ? "1" : shardManager.getShardsRunning())
                + " rodando";

        return new EmbedBuilder()
                .setAuthor("Informações sobre minha hospedagem", null, jda.getSelfUser().getAvatarUrl())
                .setColor(member.getColor())
                .setTimestamp(Instant.now())
                .addField("", "\uD83D\uDDA5 Informações do Host", false)
                .addField("\uD83D\uDEE2 Núcleos disponíveis:", "`" + Runtime.getRuntime().availableProcessors() + " cores ("
                        + SystemStatsUtils.getProcessCpuLoad() + "%)`", true)
                .addField("\uD83C\uDF9E Memória RAM", "`"
                        + SystemStatsUtils.usedMemory() +
                        "M/"
                        + SystemStatsUtils.totalMemory() + "M`", true)
                .addField("\uD83D\uDD2E Sistema Operacional", "`" + System.getProperty("os.name") + "`", true)
                .addField("\uD83D\uDED2 Empresa fornecedora:", "[HypeHost - Hospedagem Minecraft e VPS](https://hypehost.com.br)", true)
                .addField("\uD83E\uDDEA Local do Host", "`" + this.localeManager.buildMessage() + "`", true)
                .addField("<a:infinito:703187274912759899> Node", "`Gabriela`", true)
                .addField("", "\uD83D\uDCE1 Informações de conexão", false)
                .addField("🌏 Shards", "`" + shardMessage + "`", false)
                .addField("<:discord:723587554422816889> Discord Ping", "`" + toEpochMilli + "ms`", false)
                .addField("\uD83E\uDDEC Discord API", "`" + jda.getGatewayPing() + "ms`", false)
                .setFooter("Ping médio: " + ((jda.getGatewayPing() + toEpochMilli) / 2) + "ms", member.getUser().getAvatarUrl())
                .build();
    }
}
