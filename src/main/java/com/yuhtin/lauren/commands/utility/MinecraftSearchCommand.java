package com.yuhtin.lauren.commands.utility;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import com.yuhtin.lauren.service.GetConnectionFactory;
import net.dv8tion.jda.api.EmbedBuilder;
import org.json.JSONObject;

@CommandHandler(
        name = "mcname",
        type = CommandHandler.CommandType.UTILITY,
        description = "Procurar um jogador original de minecraft",
        alias = {"mcplayer", "mcskin"}
)
public class MinecraftSearchCommand extends Command {

    @Override
    protected void execute(CommandEvent event) {
        String[] arguments = event.getArgs().split(" ");
        if (event.getArgs().equalsIgnoreCase("")) {
            event.getChannel().sendMessage("<a:tchau:751941650728747140> Você precisa inserir um nome de jogador para pesquisar, exemplo `$mcname Yuhtin`.").queue();
            return;
        }

        String searchUUID = "https://api.mojang.com/users/profiles/minecraft/" + arguments[0];
        GetConnectionFactory connection = new GetConnectionFactory(searchUUID);
        String response = connection.buildConnection();
        if (response == null || response.equals("")) {
            event.getChannel().sendMessage("<:chorano:726207542413230142> Este jogador não é original ou a api está offline").queue();
            return;
        }

        JSONObject object = new JSONObject(response);
        String nick = object.getString("name");
        String uuid = object.getString("id");
        String head = "https://visage.surgeplay.com/head/" + uuid;
        String body = "https://visage.surgeplay.com/full/" + uuid;
        String skin = "https://visage.surgeplay.com/skin/" + uuid;

        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor("Informações sobre o jogador " + nick, null, head);
        builder.setFooter("Você está visualizando a skin de " + nick, event.getAuthor().getAvatarUrl());
        builder.setDescription("Para baixar esta skin [clique aqui](" + skin + ")");
        builder.setImage(body);

        event.getChannel().sendMessage(builder.build()).queue();
    }

}
