package com.yuhtin.lauren.commands.utility;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.core.statistics.controller.StatsController;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import com.yuhtin.lauren.service.GetConnectionFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import org.json.JSONObject;

import java.time.Instant;

@CommandHandler(
        name = "mcserver",
        type = CommandHandler.CommandType.UTILITY,
        description = "Ver as informações de um servidor",
        alias = {}
)
public class MinecraftServerCommand extends Command {

    @Override
    protected void execute(CommandEvent event) {
        String[] arguments = event.getArgs().split(" ");
        if (arguments.length < 1) {
            event.getChannel().sendMessage("<a:tchau:751941650728747140> Você precisa inserir um nome de jogador para pesquisar, exemplo `$mcserver hypixel.net`.").queue();
            return;
        }

        GetConnectionFactory connection = new GetConnectionFactory("https://api.mcsrvstat.us/ping/" + arguments[0]);
        String response = connection.buildConnection();
        if (response == null || response.equals("") || response.contains("getaddrinfo")) {
            event.getChannel().sendMessage("<:chorano:726207542413230142> Este servidor não foi encontrado ou a api está offline").queue();
            return;
        }

        ServerInfo serverStatus = parse(response);
        String online = serverStatus.online > 0 ? "<:certo:756770088538996847> `Online`" : "<:errado:756770088639791234> `Offline`";
        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor("Informações do servidor " + arguments[0], null, event.getGuild().getIconUrl());
        builder.setDescription("<:version:756767328334512179> **Versão**: `" + serverStatus.version + "`\n" +
                "<:sopa:756767328523517982> **Jogadores online**: `" + serverStatus.online + "/" + serverStatus.max + "`\n" +
                "<:time:756767328498090044> **Status do servidor**:  " + online);

        builder.setFooter("Comando usado as", event.getAuthor().getAvatarUrl());
        builder.setTimestamp(Instant.now());

        event.getChannel().sendMessage(builder.build()).queue();
    }


    private ServerInfo parse(String response) {
        JSONObject object = new JSONObject(response);

        JSONObject version = object.getJSONObject("version");
        JSONObject players = object.getJSONObject("players");

        return new ServerInfo(version.getString("name"), players.getInt("max"), players.getInt("online"));
    }

    @AllArgsConstructor
    static class ServerInfo {
        @Getter private final String version;
        @Getter private final int max, online;
    }
}
