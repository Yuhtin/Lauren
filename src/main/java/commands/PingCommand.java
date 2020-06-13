package commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import lombok.SneakyThrows;
import models.annotations.CommandHandler;
import net.dv8tion.jda.api.EmbedBuilder;

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
        long maxMemory = Runtime.getRuntime().maxMemory() / (1024 * 1024);
        long freeMemory = Runtime.getRuntime().freeMemory() / (1024 * 1024);
        long usedMemory = maxMemory - freeMemory;

        EmbedBuilder builder = new EmbedBuilder()
                .setAuthor("Informações de conexão", "https://google.com", event.getJDA().getSelfUser().getAvatarUrl())
                .setColor(event.getMember().getColor())
                .setTimestamp(Instant.now())
                .setDescription("• Processadores disponiveis: `" + Runtime.getRuntime().availableProcessors() + " cores`\n" +
                        "• Memoria RAM: `" + usedMemory + "mb/" + maxMemory + "mb (" + ((maxMemory * usedMemory) / 100000) + "%)`\n\n"
                        + "• Sistema Operacional: `" + System.getProperty("os.name") + "`\n"
                        + "• Arquitetura OS: `" + System.getProperty("os.arch") + "`\n"
                        + "• Local Host: `Rio de Janeiro, RJ, Brazil`\n\n"
                        + "• Discord Ping: `" + (System.currentTimeMillis() - printOne) + "ms`\n"
                        + "• Web API Ping: `" + event.getJDA().getGatewayPing() + "ms`")
                .setFooter("Ping atual: " + ((event.getJDA().getGatewayPing() + (System.currentTimeMillis() - printOne)) / 2) + "ms", event.getMember().getUser().getAvatarUrl());

        event.getChannel().sendMessage(builder.build()).queue();
    }
}
