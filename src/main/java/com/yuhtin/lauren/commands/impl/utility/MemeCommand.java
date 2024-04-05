package com.yuhtin.lauren.commands.impl.utility;

import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import com.yuhtin.lauren.service.GetConnectionFactory;
import com.yuhtin.lauren.util.EmbedUtil;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import org.json.JSONObject;

@CommandInfo(
        name = "meme",
        type = CommandInfo.CommandType.UTILITY,
        description = "Memes legais"
)
public class MemeCommand implements Command {

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) throws Exception {
        val connection = new GetConnectionFactory("https://apis.duncte123.me/meme");
        val response = connection.buildConnection();
        if (response == null) {
            hook.sendMessageEmbeds(EmbedUtil.of("Deu merda na api foi mal!")).queue();
            return;
        }

        val object = new JSONObject(response);
        val data = object.getJSONObject("data");

        val title = data.getString("title");
        val url = data.getString("url");
        val image = data.getString("image");

        val embed = new EmbedBuilder();
        embed.setTitle(title, url);
        embed.setImage(image);

        hook.sendMessageEmbeds(embed.build()).queue();
    }
}
