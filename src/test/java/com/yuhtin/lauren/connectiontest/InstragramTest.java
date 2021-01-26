package com.yuhtin.lauren.connectiontest;

import com.yuhtin.lauren.startup.Startup;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class InstragramTest {

    public static void main(String[] args) {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://instagram-utils.p.rapidapi.com/v1/profile_info?profile=davzitus")
                .get()
                .addHeader("x-rapidapi-key", "5939824860msh6f838a06fa5652fp1146eejsnb0f71065f548")
                .addHeader("x-rapidapi-host", "instagram-utils.p.rapidapi.com")
                .build();

        try {

            Response response = client.newCall(request).execute();
            JSONObject jsonObject = new JSONObject(response.body().toString()).getJSONObject("user_info");

            String biography = jsonObject.getString("biography");

            int following = jsonObject.getInt("following");
            int followers = jsonObject.getInt("followed_by");

            int posts = jsonObject.getInt("timeline_media");
            String fullName = jsonObject.getString("full_name");
            String picture = jsonObject.getString("profile_pic_url_hd");

        }catch (Exception exception) {

            System.out.println("Chora");

        }



    }

}
