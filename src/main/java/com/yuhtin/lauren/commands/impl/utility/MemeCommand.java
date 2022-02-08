package com.yuhtin.lauren.commands.impl.utility;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandData;
import com.yuhtin.lauren.service.GetConnectionFactory;
import net.dv8tion.jda.api.EmbedBuilder;
import org.json.JSONObject;

@CommandData(
        name = "meme",
        type = CommandData.CommandType.UTILITY,
        description = "Memes legais",
        alias = {}
)
public class MemeCommand implements Command {

    @Override
    protected void execute(CommandEvent event) {
        event.getChannel().sendTyping().queue();

        GetConnectionFactory connection = new GetConnectionFactory("https://apis.duncte123.me/meme");
        String response = connection.buildConnection();

        JSONObject object = new JSONObject(response);
        JSONObject data = object.getJSONObject("data");

        String title = data.getString("title"),
                url = data.getString("url"),
                image = data.getString("image");

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(title, url);
        embed.setImage(image);

        event.getChannel().sendMessage(embed.build()).queue();
    }
}
