package com.yuhtin.lauren.manager;

import com.yuhtin.lauren.application.Lauren;
import com.yuhtin.lauren.models.data.PlayerData;
import com.yuhtin.lauren.utils.serialization.Serializer;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Member;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;
import java.util.Map;

public class PlayerDataManager {

    public static PlayerData get(Long userID) {
        String data = Lauren.data.loadPlayer(userID);
        if (data == null) {
            Lauren.data.create(userID);
            return new PlayerData(userID);
        }

        return Serializer.playerData.deserialize(data);
    }



}
