package com.yuhtin.lauren.commands.utility;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.utils.helper.MathUtils;
import lombok.SneakyThrows;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.time.Instant;

@CommandHandler(name = "ping", type = CommandHandler.CommandType.UTILITY, description = "Verificar as informações da minha hospedagem")
public class PingCommand extends Command {
    public PingCommand() {
        this.name = "ping";
        this.aliases = new String[]{"pong", "delay"};
    }

    @SneakyThrows
    @Override
    protected void execute(CommandEvent event) {
        long printOne = System.currentTimeMillis();
        long maxMemory = Runtime.getRuntime().maxMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();
        long usedMemory = maxMemory - freeMemory;

        EmbedBuilder builder = new EmbedBuilder()
                .setAuthor("Informações sobre minha VPS", "https://google.com", event.getJDA().getSelfUser().getAvatarUrl())
                .setColor(event.getMember().getColor())
                .setTimestamp(Instant.now())
                .addField("", "\uD83D\uDDA5 Informações do Host", false)
                .addField("\uD83D\uDEE2 Núcleos disponíveis:", "`" + Runtime.getRuntime().availableProcessors() + " cores`", true)
                .addField("\uD83C\uDF9E Memória RAM", "`" + MathUtils.bytesToLegibleValue(usedMemory) + "/" + MathUtils.bytesToLegibleValue(maxMemory) + "`", true)
                .addField("\uD83D\uDD2E Sistema Operacional", "`" + System.getProperty("os.name") + "`", true)
                .addField("\uD83D\uDED2 Empresa fornecedora:", "[HypeHost - Hospedagem de Minecraft e VPS](https://hypehost.com.br)", true)
                .addField("\uD83E\uDDEA Local do Host", "`Lenoir, North Carolina, USA`", true)
                .addField("", "\uD83D\uDCE1 Informações de conexão", false)
                .addField("<:discord:723587554422816889> Discord Ping", "`" + (System.currentTimeMillis() - printOne) + "ms`", false)
                .addField("\uD83E\uDDEC Discord API", "`" + event.getJDA().getGatewayPing() + "ms`", false)
                .setFooter("Ping médio: " + ((event.getJDA().getGatewayPing() + (System.currentTimeMillis() - printOne)) / 2) + "ms", event.getMember().getUser().getAvatarUrl());

        event.getChannel().sendMessage(builder.build()).queue();
    }
}
