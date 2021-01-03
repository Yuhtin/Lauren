package com.yuhtin.lauren.service;

import com.yuhtin.lauren.core.statistics.StatsController;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetConnectionFactory {

    @Inject private static StatsController statsController;

    final String url;

    public GetConnectionFactory(String url) {
        this.url = url;
    }

    @Nullable
    public String buildConnection() {
        String line;
        BufferedReader reader;
        StringBuilder responseContent = new StringBuilder();

        try {
            URL url = new URL(this.url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = reader.readLine()) != null) responseContent.append(line);
            reader.close();

            statsController.getStats("Requests Externos").suplyStats(1);
        } catch (IOException exception) {
            return null;
        }

        return responseContent.toString();
    }
}
