package com.yuhtin.lauren.commands.help;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import com.yuhtin.lauren.service.LocaleManager;
import com.yuhtin.lauren.service.PterodactylConnection;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.time.Instant;

@CommandHandler(
        name = "host",
        type = CommandHandler.CommandType.HELP,
        description = "Verificar as informações da minha hospedagem",
        alias = {"pong", "delay", "ping"})
public class PingCommand extends Command {

    @SneakyThrows
    @Override
    protected void execute(CommandEvent event) {
        long actual = System.currentTimeMillis();
        event.getChannel().sendMessage("Carregando...").queue(message -> {
            MessageEmbed embed = createEmbed(
                    message.getTimeCreated().toInstant().toEpochMilli() - actual,
                    event.getMember(),
                    event.getJDA()
            );

            message.editMessage(embed).queue();
        });
    }

    private MessageEmbed createEmbed(long toEpochMilli, Member member, JDA jda) {

        String shardMessage = LaurenStartup.getInstance().getBot().getShardsTotal()
                + " shards, "
                + LaurenStartup.getInstance().getBot().getShardsRunning()
                + " rodando";

        EmbedBuilder builder = new EmbedBuilder()
                .setAuthor("Informações sobre minha hospedagem", null, jda.getSelfUser().getAvatarUrl())
                .setColor(member.getColor())
                .setTimestamp(Instant.now())
                .addField("", "\uD83D\uDDA5 Informações do Host", false)
                .addField("\uD83D\uDEE2 Núcleos disponíveis:", "`" + Runtime.getRuntime().availableProcessors() + " cores ("
                        + PterodactylConnection.get().getServer().getServerUsage().getCpuUsage() +
                        "%)`", true)
                .addField("\uD83C\uDF9E Memória RAM", "`"
                        + PterodactylConnection.get().getServer().getServerUsage().getMemoryUsage() +
                        "M/"
                        + PterodactylConnection.get().getServer().getLimits().getMemory() + "M`", true)
                .addField("\uD83D\uDD2E Sistema Operacional", "`" + System.getProperty("os.name") + "`", true)
                .addField("\uD83D\uDED2 Empresa fornecedora:", "[HypeHost - Hospedagem Minecraft e VPS](https://hypehost.com.br)", true)
                .addField("\uD83E\uDDEA Local do Host", "`" + LocaleManager.getInstance().buildMessage() + "`", true)
                .addField("<a:infinito:703187274912759899> Node", "`Gabriela`", true)
                .addField("", "\uD83D\uDCE1 Informações de conexão", false)
                .addField("🌏 Shards", "`" + shardMessage + "`", false)
                .addField("<:discord:723587554422816889> Discord Ping", "`" + toEpochMilli + "ms`", false)
                .addField("\uD83E\uDDEC Discord API", "`" + jda.getGatewayPing() + "ms`", false)
                .setFooter("Ping médio: " + ((jda.getGatewayPing() + toEpochMilli) / 2) + "ms", member.getUser().getAvatarUrl());
        return builder.build();
    }
}
