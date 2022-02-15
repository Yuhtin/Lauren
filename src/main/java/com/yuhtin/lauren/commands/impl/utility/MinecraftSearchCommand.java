package com.yuhtin.lauren.commands.impl.utility;

import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import com.yuhtin.lauren.service.GetConnectionFactory;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import org.json.JSONObject;

@CommandInfo(
        name = "minecraft.player",
        type = CommandInfo.CommandType.UTILITY,
        description = "Procurar um jogador original de minecraft",
        args = {
                "<player_name>-Nome do jogador no Minecraft: Java Edition"
        }
)
public class MinecraftSearchCommand implements Command {

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) throws Exception {
        val playerName = event.getOption("player_name").getAsString();

        val searchUUID = "https://api.mojang.com/users/profiles/minecraft/" + playerName;
        val connection = new GetConnectionFactory(searchUUID);
        val response = connection.buildConnection();
        if (response == null || response.equals("")) {
            hook.sendMessage("<:chorano:726207542413230142> Este jogador não é original ou a api está offline.").queue();
            return;
        }

        val object = new JSONObject(response);
        val nick = object.getString("name");
        val uuid = object.getString("id");
        val head = "https://visage.surgeplay.com/head/" + uuid;
        val body = "https://visage.surgeplay.com/full/" + uuid;
        val skin = "https://visage.surgeplay.com/skin/" + uuid;

        val builder = new EmbedBuilder();
        builder.setAuthor("Informações sobre o jogador " + nick, null, head);
        builder.setFooter("Você está visualizando a skin de " + nick, head);
        builder.setDescription("Para baixar esta skin [clique aqui](" + skin + ")");
        builder.setImage(body);

        hook.sendMessageEmbeds(builder.build()).queue();
    }

}
