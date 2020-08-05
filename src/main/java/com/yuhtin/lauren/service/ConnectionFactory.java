package com.yuhtin.lauren.service;

import com.yuhtin.lauren.core.logger.Logger;
import lombok.Getter;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

@Setter
@Getter
public class ConnectionFactory {

    private String API;

    private HttpURLConnection finalConnection;
    private String METHOD = "POST";
    private String TYPE = "application/x-www-form-urlencoded";
    private String USER_AGENT = "Mozila/5.0";
    private String data = "";

    private Map<String, String> fields;

    public ConnectionFactory(Map<String, String> fields, String url) {
        this.API = url;
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

                BufferedReader reader = new BufferedReader(new InputStreamReader(readWithAccess(new URL(API))));
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }

                return content.toString();
            } catch (Exception exception) {
                Logger.error(exception);
            }
        }

        return null;
    }

    public InputStream readWithAccess(URL url) {
        try {
            byte[] out = data.getBytes();
            finalConnection = (HttpURLConnection) url.openConnection();
            finalConnection.setRequestMethod(METHOD);
            finalConnection.setDoOutput(true);
            finalConnection.addRequestProperty("User-Agent", USER_AGENT);
            finalConnection.addRequestProperty("Content-Type", TYPE);
            finalConnection.connect();
            try {
                OutputStream outputStream = finalConnection.getOutputStream();
                outputStream.write(out);
            } catch (Exception exception) {
                Logger.log("Failed to get outputstream from post");
            }

            return finalConnection.getInputStream();
        } catch (Exception exception) {
            Logger.log("Failed to build finalConnection");
            return null;
        }
    }
}
