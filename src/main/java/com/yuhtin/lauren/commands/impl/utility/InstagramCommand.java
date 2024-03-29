package com.yuhtin.lauren.commands.impl.utility;

import com.google.inject.Inject;
import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import com.yuhtin.lauren.core.statistics.StatsController;
import com.yuhtin.lauren.utils.SimpleEmbed;
import com.yuhtin.lauren.utils.TimeUtils;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@CommandInfo(
        name = "instagram",
        type = CommandInfo.CommandType.UTILITY,
        description = "Bisbilhotar o perfil dos outros",
        args = {
                "<account>-Conta do instagram que deseja ver"
        }
)
public class InstagramCommand implements Command {

    private static final Map<Long, Long> DELAYS = new HashMap<>();

    @Inject private StatsController statsController;

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) throws Exception {
        val delay = DELAYS.getOrDefault(event.getMember().getIdLong(), 0L);
        if (delay > System.currentTimeMillis()) {
            hook.sendMessage("<:chorano:726207542413230142> Aguarde mais "
                    + TimeUtils.formatTime(delay - System.currentTimeMillis())
                    + " para usar este comando."
            ).queue();

            return;
        }

        val instagram = event.getOption("account").getAsString().replace(" ", "_");
        DELAYS.put(event.getMember().getIdLong(), Instant.now().plusMillis(TimeUnit.MINUTES.toMillis(1)).toEpochMilli());

        val client = new OkHttpClient();
        val request = new Request.Builder()
                .url("https://instagram.com/" + instagram + "/?__a=1")
                .get()
                .build();

        statsController.getStats("Requests Externos").suplyStats(1);

        try {

            val response = client.newCall(request).execute();

            String line;
            val content = new StringBuilder();

            val reader = new BufferedReader(new InputStreamReader(response.body().byteStream()));
            while ((line = reader.readLine()) != null) content.append(line);
            reader.close();

            val jsonObject = new JSONObject(content.toString()).getJSONObject("graphql").getJSONObject("user");

            val username = jsonObject.getString("username");
            val biography = jsonObject.getString("biography");

            val following = jsonObject.getJSONObject("edge_follow").getInt("count");
            val followers = jsonObject.getJSONObject("edge_followed_by").getInt("count");

            val fullName = jsonObject.getString("full_name");
            val picture = jsonObject.getString("profile_pic_url_hd");

            val timelineMedia = jsonObject.getJSONObject("edge_owner_to_timeline_media");
            val posts = timelineMedia.getInt("count");

            val edges = timelineMedia.getJSONArray("edges");
            val lastPost = edges.isEmpty() ? "" : edges.getJSONObject(0).getJSONObject("node").getString("display_url");

            val embed = new EmbedBuilder();
            embed.setColor(SimpleEmbed.getColor());
            embed.setTitle("Instagram de " + fullName, "https://www.instagram.com/" + username);
            embed.setThumbnail(picture);
            embed.setImage(lastPost);
            embed.setDescription("**Nome**: `" + fullName + "`\n" +
                    "**Bio**: \n`" + biography + "`\n\n" +
                    "**Seguidores**: `" + followers + "`\n" +
                    "**Seguindo**: `" + following + "`\n" +
                    "**Uploads**: `" + posts + "`"
            );

            embed.setFooter("Comando usado as", event.getUser().getAvatarUrl());
            embed.setTimestamp(Instant.now());

            hook.sendMessageEmbeds(embed.build()).setEphemeral(true).queue();

        } catch (Exception exception) {
            exception.printStackTrace();
            hook.sendMessage("<:eita:764084277226373120> Esse perfil ai não tem no meu livro não, tenta outro").queue();
        }
    }

}
