package models.data;

import application.Lauren;
import models.cache.PlayerDataCache;
import enums.GameType;
import models.cache.MatchCache;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import utils.helper.Utilities;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class Match {

    public String id, urlPrint;
    public GameType type;
    public Long winPlayer, startTime, finishTime;
    public TextChannel channel;
    public List<Long> players, confirmedPlayers = new ArrayList<>();

    public Match(GameType type, Long startTime) {
        this.type = type;
        this.startTime = startTime;

        id = Utilities.randomString();
    }

    public void createChannel() {
        Lauren.guild.createTextChannel("match-" + id)
                .addPermissionOverride(Lauren.guild.getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL))
                .queue(text -> {
                    channel = text;
                });
    }

    public boolean startMatch() {
        if (players.size() > confirmedPlayers.size())
            return false;

        MatchCache.insert(this);
        return true;
    }

    public void finishMatch(Long winPlayer, String urlPrint) {
        this.winPlayer = winPlayer;
        this.urlPrint = urlPrint;
        this.finishTime = System.currentTimeMillis();

        players.forEach(id -> PlayerDataCache.get(id).computMatch(this).save());

        MatchCache.finishMatch(this);
    }

    public boolean containsPlayer(Long userID) {
        return players.contains(userID);
    }

    public void insertPlayer(Long userID) {
        players.add(userID);
    }
}
