package dao;

import dao.controller.PlayerDataController;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Member;

import java.util.LinkedHashMap;
import java.util.Map;

public class PlayerData {

    @Getter
    private static final Map<Long, PlayerDataController> DATA = new LinkedHashMap<>();

    public static PlayerDataController get(Member member) {
        return get(member.getIdLong());
    }

    public static PlayerDataController get(Long userID) {
        if (!DATA.containsKey(userID)) DATA.put(userID, new PlayerDataController(userID));

        return DATA.get(userID);
    }

    public static void insert(PlayerDataController controller) {
        DATA.put(controller.userID, controller);
    }

}
