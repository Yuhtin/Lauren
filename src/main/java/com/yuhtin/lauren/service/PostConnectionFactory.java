package com.yuhtin.lauren.service;

import com.google.inject.Inject;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.statistics.StatsController;
import lombok.Data;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

@Data
public class PostConnectionFactory {

    @Inject private static Logger logger;
    @Inject private static StatsController statsController;

    private String api;

    private HttpURLConnection finalConnection;
    private String method = "POST";
    private String type = "application/x-www-form-urlencoded";
    private String userAgent = "Mozila/5.0";
    private String data = "";

    private Map<String, String> fields;

    public PostConnectionFactory(Map<String, String> fields, String url) {
        this.api = url;
        this.fields = fields;
    }

    public String buildConnection() {
        StringBuilder content = new StringBuilder();
        if (!fields.isEmpty()) {
            String line;
            try {
                for (Map.Entry<String, String> entry : fields.entrySet())
                    data += ("&" + entry.getKey() + "=" + entry.getValue());
                data = data.replaceFirst("&", "");

                BufferedReader reader = new BufferedReader(new InputStreamReader(readWithAccess(new URL(api))));
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }

                statsController.getStats("Requests Externos").suplyStats(1);
                return content.toString();
            } catch (Exception exception) {
                logger.log(LogType.WARNING, "Try to execute connection", exception);
            }
        }

        return null;
    }

    public InputStream readWithAccess(URL url) {
        try {
            byte[] out = data.getBytes();
            finalConnection = (HttpURLConnection) url.openConnection();
            finalConnection.setRequestMethod(method);
            finalConnection.setDoOutput(true);
            finalConnection.addRequestProperty("User-Agent", userAgent);
            finalConnection.addRequestProperty("Content-Type", type);
            finalConnection.connect();
            try {
                OutputStream outputStream = finalConnection.getOutputStream();
                outputStream.write(out);
            } catch (Exception exception) {
                logger.log(LogType.WARNING, "Try to execute connection", exception);
            }

            return finalConnection.getInputStream();
        } catch (Exception exception) {
            logger.log(LogType.WARNING, "Try to execute connection", exception);
            return null;
        }
    }
}
