package com.yuhtin.lauren.models.objects;

import com.yuhtin.lauren.startup.Startup;
import com.yuhtin.lauren.utils.serialization.Serializer;
import lombok.Data;

import java.io.*;

@Data
public class Config {

    private boolean laurenTest;

    private long ownerID;
    private long resgistrationId;
    private long valorantCasual;
    private long valorantRanked;
    private long poolCasual;
    private long poolRanked;

    private String prefix;
    private String token;
    private String formatNickname;
    private String pastebinDevKey;
    private String pteroKey;
    private String pastebinUserKey;
    private String geoIpAcessKey;
    private String instagramApiKey;

    // Connection info
    private String databaseType;
    private String username;
    private String host;
    private String password;
    private String database;
    private String sqlFile;

    public static Config loadConfig(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {

                if (!file.createNewFile()) return null;

                Config config = new Config();
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file.getAbsolutePath()))) {
                    writer.write(Serializer.getConfig().serialize(config));
                    writer.newLine();
                    writer.flush();
                }

                Startup.getLauren().getLogger().warning("Put a valid token in the bot's config");
                return null;
            }

            String line;
            StringBuilder responseContent = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(file.getAbsolutePath()))) {
                while ((line = reader.readLine()) != null) responseContent.append(line);
            }

            return Serializer.getConfig().deserialize(responseContent.toString());
        } catch (Exception exception) {
            return null;
        }
    }

}
