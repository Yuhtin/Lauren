package com.yuhtin.lauren.core.match.controller;

import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.match.Game;
import com.yuhtin.lauren.core.match.Match;
import com.yuhtin.lauren.models.enums.GameMode;
import com.yuhtin.lauren.models.enums.GameType;
import net.dv8tion.jda.api.entities.Member;

import javax.annotation.Nullable;
import java.util.*;

public class MatchController {

    public static final Map<String, Match> matches = new HashMap<>();
    public static final Map<Game, LinkedList<Long>> row = new HashMap<>();
    public static final Map<String, Match> pastMatches = new HashMap<>();

    public static void startup() {
        for (GameType type : GameType.values()) {
            for (GameMode mode : GameMode.values()) {
                row.put(new Game(type, mode), new LinkedList<>());
            }
        }
    }

    public static List<Long> getByType(GameType type, GameMode mode) {
        return row.get(row.keySet().stream().filter(game -> game.type == type && game.mode == mode).findFirst().orElse(null));
    }

    @Nullable
    public static Match getByPlayer(Member member) {
        Match match = null;
        for (String id : matches.keySet()) {
            Match current = matches.get(id);
            if (current.containsPlayer(member.getIdLong())) match = current;
        }

        return match;
    }


    public static void insert(Match data) {
        if (data.finishTime != 0) pastMatches.put(data.id, data);
        else matches.put(data.id, data);
    }

    public static void finishMatch(Match match) {
        matches.remove(match.id);
        pastMatches.put(match.id, match);
    }

    public static void findMatch() {
        Map<Game, Match> newMatches = new HashMap<>();

        row.forEach((game, users) -> {
            //if (!newMatches.containsKey(game) && users.size() >= game.type.minPlayers) {
            if (users.size() > 0) {
                Logger.log("The game " + game + " has " + users.size() + " players").save();
                Match match = new Match(game);

                List<Long> selectedPlayers = new ArrayList<>();
                for (int i = 0; i < Math.min(game.type.minPlayers, users.size()); i++) {
                    selectedPlayers.add(users.get(i));
                    users.remove(i);
                }
                match.players = selectedPlayers;
                match.createChannel();

                new Thread(() -> newMatches.put(game, match)).start();
            }
        });
    }
}
