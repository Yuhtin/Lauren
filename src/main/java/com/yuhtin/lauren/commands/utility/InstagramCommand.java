package com.yuhtin.lauren.commands.utility;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import com.yuhtin.lauren.service.GetConnectionFactory;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
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
        if (!event.getMember().hasPermission(Permission.MESSAGE_TTS)) {

            event.getChannel().sendMessage("<:chorano:726207542413230142> Comando em manutenção :(").queue();
            return;

        }

        String instagram;
        if (event.getArgs().equalsIgnoreCase("")) {
            event.getChannel().sendMessage("<:fodane:764085078187442176> Utiliza o comando direito amigo, assim ó `$instagram <perfil>`").queue();
            return;
        }

        event.getChannel().sendTyping().queue();
        instagram = event.getArgs().replace(" ", "_");

        GetConnectionFactory connection = new GetConnectionFactory("https://www.instagram.com/" + instagram + "/?__a=1");

        String response = connection.buildConnection();
        if (response == null || response.equalsIgnoreCase("") || !response.contains("{")) {
            event.getChannel().sendMessage("<:eita:764084277226373120> Esse perfil ai não tem no meu livro não, tenta outro").queue();
            return;
        }

        JSONObject object = new JSONObject(response);
        JSONObject data = object.getJSONObject("graphql").getJSONObject("user");

        String username = data.getString("username"),
                name = data.getString("full_name"),
                biography = data.getString("biography"),
                picture = data.getString("profile_pic_url_hd");

        JSONObject pictures = data.getJSONObject("edge_owner_to_timeline_media");

        int followers = data.getJSONObject("edge_followed_by").getInt("count"),
                following = data.getJSONObject("edge_follow").getInt("count"),
                uploads = pictures.getInt("count");

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

        embed.setImage(getLatestImage(pictures.getJSONArray("edges")));

        event.getChannel().sendMessage(embed.build()).queue();
    }

    private String getLatestImage(JSONArray images) {
        if (images.length() == 0) return null;

        JSONObject object = new JSONObject(images.get(0).toString()).getJSONObject("node");
        return object.getString("thumbnail_src");
    }
}
