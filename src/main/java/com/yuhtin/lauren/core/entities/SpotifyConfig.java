package com.yuhtin.lauren.core.entities;

import com.wrapper.spotify.SpotifyApi;
import com.yuhtin.lauren.application.Lauren;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.models.enums.LogType;
import com.yuhtin.lauren.utils.helper.SpotifyConnector;
import com.yuhtin.lauren.utils.serialization.Serializer;
import lombok.Setter;

import java.io.*;

@Setter
public class SpotifyConfig {
    public String clientId, clientSecret, acessToken, refreshToken;
    private static final Serializer<SpotifyConfig> serializer = new Serializer<>(SpotifyConfig.class);

    public static SpotifyApi construct() {
        try {
            File file = new File("config/spotify.json");
            if (!file.exists()) {

                if (!file.createNewFile()) return null;
                SpotifyConfig config = new SpotifyConfig();

                BufferedWriter writer = new BufferedWriter(new FileWriter(file.getAbsolutePath()));
                writer.write(serializer.serialize(config));
                writer.newLine();
                writer.flush();

                Logger.log("Please configure the spotify.json file correctly", LogType.ERROR).save();
                return null;
            }

            BufferedReader reader = new BufferedReader(new FileReader(file.getAbsolutePath()));

            SpotifyConfig config = serializer.deserialize(reader.readLine());
            return SpotifyConnector.get(config.clientId, config.clientSecret, config.acessToken, config.refreshToken);
        } catch (Exception exception) {
            return null;
        }
    }
}
