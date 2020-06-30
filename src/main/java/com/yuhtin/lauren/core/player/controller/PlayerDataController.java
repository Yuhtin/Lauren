package com.yuhtin.lauren.core.player.controller;

import com.yuhtin.lauren.application.Lauren;
import com.yuhtin.lauren.core.player.PlayerData;
import com.yuhtin.lauren.utils.helper.Utilities;
import com.yuhtin.lauren.utils.serialization.Serializer;

public class PlayerDataController {

    public static PlayerData get(long userID) {
        String data = Lauren.data.loadPlayer(userID);
        if (data == null) {
            Lauren.data.create(userID);
            Utilities.updateNickByLevel(userID, 0);
            return new PlayerData(userID);
        }
        if (data.equalsIgnoreCase("SQLError")) return new PlayerData(userID);

        return Serializer.playerData.deserialize(data);
    }



}
