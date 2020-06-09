package matches;

import data.controller.PlayerDataController;
import enums.GameType;

public class Match {

    public GameType type;
    public Long winPlayer;
    public Long player1, player2, player3, player4, startTime, finishTime;
    public String urlPrint;

    public Match(GameType type, Long player1, Long player2, Long player3, Long player4, Long startTime) {
        this.type = type;
        this.player1 = player1;
        this.player2 = player2;
        this.player3 = player3;
        this.player4 = player4;
        this.startTime = startTime;
    }

    public void finishMatch(Long winPlayer, String urlPrint) {
        this.winPlayer = winPlayer;
        this.urlPrint = urlPrint;
        this.finishTime = System.currentTimeMillis();

        PlayerDataController.get(player1).computMatch(this).save();
        PlayerDataController.get(player2).computMatch(this).save();
    }
}
