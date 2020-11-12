package com.yuhtin.lauren.core.entities;

import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.models.enums.LogType;
import com.yuhtin.lauren.utils.serialization.Serializer;
import lombok.Setter;

import java.io.*;

@Setter
public class Config {
    public String prefix, token, formatNickname,
            databaseType, mySqlUser, mySqlHost,
            mySqlPassword, mySqlDatabase, pastebinDevKey,
            pteroKey, pastebinUserKey, vagalumeKey = "";

    public long ownerID, resgistrationId, valorantCasual, valorantRanked, poolCasual, poolRanked = 0;
    public boolean log = false, laurenTest = false;

    public static Config startup() {
        try {
            File file = new File("config/config.json");
            if (!file.exists()) {

                if (!file.createNewFile()) return null;
                Config config = new Config();
                config.setPrefix("$");
                config.setToken("Put token here");
                config.setResgistrationId(704303594211639356L);
                config.setOwnerID(272879983326658570L);
                config.setFormatNickname("[@level] ");
                config.setDatabaseType("SQLite");
                config.setLog(true);

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file.getAbsolutePath()))) {
                    writer.write(Serializer.getConfig().serialize(config));
                    writer.newLine();
                    writer.flush();
                }

                Logger.log("Put a valid token in the bot's config", LogType.WARN);
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
