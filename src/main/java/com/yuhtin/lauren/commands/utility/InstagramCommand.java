package com.yuhtin.lauren.commands.utility;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import com.yuhtin.lauren.models.enums.LogType;
import com.yuhtin.lauren.service.GetConnectionFactory;
import net.dv8tion.jda.api.EmbedBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

@CommandHandler(
        name = "instagram",
        type = CommandHandler.CommandType.UTILITY,
        description = "Bisbilhotar o perfil dos outros",
        alias = {"insta"}
)
public class InstagramCommand extends Command {

    @Override
    protected void execute(CommandEvent event) {
        String instagram;
        if (event.getArgs().equalsIgnoreCase("")) {
            event.getChannel().sendMessage("<:fodane:764085078187442176> Utiliza o comando direito amigo, assim ó `$instagram <perfil>`").queue();
            return;
        }

        event.getChannel().sendTyping().queue();
        instagram = event.getArgs().replace(" ", "_");
        GetConnectionFactory connection = new GetConnectionFactory("https://apis.duncte123.me/insta/" + instagram);
        String response = connection.buildConnection();

        if (!response.contains("{")) {
            event.getChannel().sendMessage("<:eita:764084277226373120> Minha api deu uma travadinha, segura um pouco ai").queue();
            return;
        }

        JSONObject object = new JSONObject(response);
        boolean success = object.getBoolean("success");
        if (!success) {
            event.getChannel().sendMessage("<:eita:764084277226373120> Esse perfil ai não tem no meu livro não, tenta outro").queue();
            return;
        }

        JSONObject data = object.getJSONObject("user");

        String username = data.getString("username"),
                name = data.getString("full_name"),
                biography = data.getString("biography"),
                picture = data.getString("profile_pic_url");

        int followers = data.getJSONObject("followers").getInt("count"),
                following = data.getJSONObject("following").getInt("count"),
                uploads = data.getJSONObject("uploads").getInt("count");

        boolean isPrivate = data.getBoolean("is_private");

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Instagram de " + username, "https://www.instagram.com/" + username);
        embed.setThumbnail(picture);
        embed.setDescription("**Nome**: " + name + "\n" +
                "**Bio**: " + biography + "\n" +
                "**Tipo**: " + (isPrivate ? "<:errado:756770088639791234> Privado" : "<:certo:756770088538996847> Público") + "\n" +
                "**Seguidores**: " + followers + "\n" +
                "**Seguindo**: " + following + "\n" +
                "**Uploads**: " + uploads);

        embed.setImage(getLatestImage(object.getJSONArray("images")));

        event.getChannel().sendMessage(embed.build()).queue();
    }

    private String getLatestImage(JSONArray images) {
        if (images.length() == 0) return null;

        JSONObject object = new JSONObject(images.get(0).toString());
        return object.getString("url");
    }
}
