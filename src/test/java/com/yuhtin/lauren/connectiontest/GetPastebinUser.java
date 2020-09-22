package com.yuhtin.lauren.connectiontest;

import com.yuhtin.lauren.service.PostConnectionFactory;

import java.util.HashMap;
import java.util.Map;

public class GetPastebinUser {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        Map<String, String> fields = new HashMap<>();
        fields.put("api_dev_key", "");
        fields.put("api_user_name", "Yuhtin");
        fields.put("api_user_password", "");

        PostConnectionFactory factory = new PostConnectionFactory(fields, "https://pastebin.com/api/api_login.php");
        String response = factory.buildConnection();

        System.out.println(factory.getData());
        System.out.println(response);

        System.out.println("Total millis> " + (System.currentTimeMillis() - start));
    }
}
