package objects.configuration;

import logger.Logger;
import lombok.Setter;
import utils.serialization.ConfigGson;

import java.io.*;

@Setter
public class Config {
    public String prefix, token, formatNickname, mySqlUser, mySqlPassword, mySqlDatabase, mySqlHost, mongoPassword, databaseType;
    public long resgistrationId;
    public boolean log = false;

    public static Config startup() {
        try {
            File file = new File("config/config.json");
            if (!file.exists()) {

                if (!file.createNewFile()) return null;
                Config config = new Config();
                config.setPrefix("$");
                config.setToken("COLOQUE O TOKEN AQUI");
                config.setResgistrationId(704303594211639356L);
                config.setFormatNickname("[@level] ");
                config.setDatabaseType("SQLite");

                BufferedWriter writer = new BufferedWriter(new FileWriter(file.getAbsolutePath()));
                writer.write(ConfigGson.serialize(config));
                writer.newLine();
                writer.flush();

                Logger.log("Put a valid token in the bot's config").save();
                return null;
            }
            BufferedReader reader = new BufferedReader(new FileReader(file.getAbsolutePath()));
            return ConfigGson.deserialize(reader.readLine());
        } catch (Exception exception) {
            return null;
        }
    }

    public void updateConfig() throws IOException {
        File file = new File("config/config.json");
        BufferedWriter writer = new BufferedWriter(new FileWriter(file.getAbsolutePath()));
        writer.write(ConfigGson.serialize(this));
        writer.newLine();
        writer.close();
    }

    public void updatePrefix(String newPrefix) {
        prefix = newPrefix;
        try {
            updateConfig();
        } catch (Exception ignored) {
        }
    }
}
