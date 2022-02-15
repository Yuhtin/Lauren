package com.yuhtin.lauren.commands.impl.utility;

import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import com.yuhtin.lauren.service.GetConnectionFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import org.json.JSONObject;

import java.time.Instant;

@CommandInfo(
        name = "minecraft.server",
        type = CommandInfo.CommandType.UTILITY,
        description = "Ver as informações de um servidor",
        args = {
                "<server_ip>-IP do servidor desejado"
        }
)
public class MinecraftServerCommand implements Command {

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) throws Exception {
        val serverIP = event.getOption("server_ip").getAsString();
        val connection = new GetConnectionFactory("https://api.mcsrvstat.us/ping/" + serverIP);
        val response = connection.buildConnection();
        if (response == null || response.equals("") || response.contains("getaddrinfo")) {
            hook.sendMessage("<:chorano:726207542413230142> Este servidor não foi encontrado ou a api está offline").queue();
            return;
        }

        val serverStatus = parse(response);
        val online = serverStatus.online > 0 ? "<:certo:756770088538996847> `Online`" : "<:errado:756770088639791234> `Offline`";
        val builder = new EmbedBuilder();
        builder.setAuthor("Informações do servidor " + serverIP, null, event.getGuild().getIconUrl());
        builder.setDescription("<:version:756767328334512179> **Versão**: `" + serverStatus.version + "`\n" +
                "<:sopa:756767328523517982> **Jogadores online**: `" + serverStatus.online + "/" + serverStatus.max + "`\n" +
                "<:time:756767328498090044> **Status do servidor**:  " + online);

        builder.setFooter("Comando usado as", event.getUser().getAvatarUrl());
        builder.setTimestamp(Instant.now());

        hook.sendMessageEmbeds(builder.build()).queue();
    }


    private ServerInfo parse(String response) {
        val object = new JSONObject(response);

        val version = object.getJSONObject("version");
        val players = object.getJSONObject("players");

        return new ServerInfo(version.getString("name"), players.getInt("max"), players.getInt("online"));
    }

    @Getter
    @AllArgsConstructor
    static class ServerInfo {

        private final String version;

        private final int max;
        private final int online;

    }
}
