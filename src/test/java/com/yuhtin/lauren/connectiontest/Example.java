package com.yuhtin.lauren.connectiontest;

import com.yuhtin.lauren.service.PostConnectionFactory;

import java.util.HashMap;
import java.util.Map;

public class Example {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        Map<String, String> fields = new HashMap<>();
        fields.put("api_dev_key", "");
        fields.put("api_user_key", "");
        fields.put("api_paste_code", "test");
        fields.put("api_paste_private", "1");
        fields.put("api_paste_name", "Lauren test");
        fields.put("api_paste_expire_date", "10M");
        fields.put("api_paste_format", "java");
        fields.put("api_option", "paste");

        PostConnectionFactory factory = new PostConnectionFactory(fields, "https://pastebin.com/api/api_post.php");
        String response = factory.buildConnection();

        System.out.println(factory.getData());
        System.out.println(response);

        System.out.println("Total millis> " + (System.currentTimeMillis() - start));
    }
}
