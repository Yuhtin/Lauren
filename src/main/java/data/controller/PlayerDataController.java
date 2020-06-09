package data.controller;

import application.Lauren;
import data.PlayerData;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Member;

import java.util.LinkedHashMap;
import java.util.Map;

public class PlayerDataController {

    @Getter
    private static final Map<Long, PlayerData> DATA = new LinkedHashMap<>();

    public static PlayerData get(Member member) {
        return get(member.getIdLong());
    }

    public static PlayerData get(Long userID) {
        if (!DATA.containsKey(userID)) {
            DATA.put(userID, new PlayerData(userID));
            Lauren.data.create(userID);
        }

        return DATA.get(userID);
    }

    public static void insert(PlayerData controller) {
        DATA.put(controller.userID, controller);
    }

}
