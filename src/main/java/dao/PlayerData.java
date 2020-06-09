package dao;

import dao.controller.PlayerDataController;
import net.dv8tion.jda.api.entities.Member;

import java.util.LinkedHashMap;
import java.util.Map;

public class PlayerData {

    private static final Map<Long, PlayerDataController> DATA = new LinkedHashMap<>();

    public static PlayerDataController get(Member member) {
        return get(member.getIdLong());
    }

    public static PlayerDataController get(Long userID) {
        if (!DATA.containsKey(userID)) DATA.put(userID, new PlayerDataController(userID));

        return DATA.get(userID);
    }
}
