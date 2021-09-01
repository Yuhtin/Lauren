package com.yuhtin.lauren.commands.impl.utility;

import com.google.inject.Inject;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.core.statistics.StatsController;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import com.yuhtin.lauren.startup.Startup;
import com.yuhtin.lauren.utils.helper.TimeUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@CommandHandler(
        name = "instagram",
        type = CommandHandler.CommandType.UTILITY,
        description = "Bisbilhotar o perfil dos outros",
        alias = {"insta"}
)
public class InstagramCommand extends Command {

    private static final Map<Long, Long> DELAYS = new HashMap<>();

    @Inject private StatsController statsController;

    @Override
    protected void execute(CommandEvent event) {

        long delay = DELAYS.getOrDefault(event.getMember().getIdLong(), 0L);
        if (delay > System.currentTimeMillis()) {

            event.getChannel().sendMessage("<:chorano:726207542413230142> Aguarde mais "
                            + TimeUtils.formatTime(delay - System.currentTimeMillis())
                            + " para usar este comando."
            ).queue();
            return;

        }

        String instagram;
        if (event.getArgs().equalsIgnoreCase("")) {
            event.getChannel().sendMessage("<:fodane:764085078187442176> Utiliza o comando direito amigo, assim ó `$instagram <perfil>`").queue();
            return;
        }

        DELAYS.put(event.getMember().getIdLong(), Instant.now().plusMillis(TimeUnit.MINUTES.toMillis(1)).toEpochMilli());

        event.getChannel().sendTyping().queue();
        instagram = event.getArgs().replace(" ", "_");

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://instagram-utils.p.rapidapi.com/v1/profile_info?profile=" + instagram)
                .get()
                .addHeader("x-rapidapi-key", Startup.getLauren().getConfig().getInstagramApiKey())
                .addHeader("x-rapidapi-host", "instagram-utils.p.rapidapi.com")
                .build();

        statsController.getStats("Requests Externos").suplyStats(1);

        try {

            Response response = client.newCall(request).execute();

            String line;
            StringBuilder content = new StringBuilder();

            BufferedReader reader = new BufferedReader(new InputStreamReader(response.body().byteStream()));
            while ((line = reader.readLine()) != null) content.append(line);
            reader.close();

            JSONObject jsonObject = new JSONObject(content.toString()).getJSONObject("user_info");

            String username = jsonObject.getString("username");
            String biography = jsonObject.getString("biography");

            int following = jsonObject.getInt("following");
            int followers = jsonObject.getInt("followed_by");

            int posts = jsonObject.getInt("timeline_media");
            String fullName = jsonObject.getString("full_name");
            String picture = jsonObject.getString("profile_pic_url_hd");

            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Instagram de " + fullName, "https://www.instagram.com/" + username);
            embed.setThumbnail(picture);
            embed.setDescription("**Nome**: " + name + "\n" +
                    "**Bio**: " + biography + "\n" +
                    "**Seguidores**: " + followers + "\n" +
                    "**Seguindo**: " + following + "\n" +
                    "**Uploads**: " + posts);

            event.getChannel().sendMessage(embed.build()).queue();

        }catch (Exception exception) {

            event.getChannel().sendMessage("<:eita:764084277226373120> Esse perfil ai não tem no meu livro não, tenta outro").queue();
            return;

        }

    }

}
