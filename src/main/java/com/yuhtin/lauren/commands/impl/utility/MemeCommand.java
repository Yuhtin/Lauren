package com.yuhtin.lauren.commands.impl.utility;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import com.yuhtin.lauren.service.GetConnectionFactory;
import net.dv8tion.jda.api.EmbedBuilder;
import org.json.JSONObject;

@CommandHandler(
        name = "meme",
        type = CommandHandler.CommandType.UTILITY,
        description = "Memes legais",
        alias = {}
)
public class MemeCommand extends Command {

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
