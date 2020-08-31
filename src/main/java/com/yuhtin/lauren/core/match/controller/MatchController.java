package com.yuhtin.lauren.core.match.controller;

import com.yuhtin.lauren.core.match.Game;
import com.yuhtin.lauren.core.match.Match;
import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.service.PlayerService;
import com.yuhtin.lauren.models.enums.GameMode;
import com.yuhtin.lauren.models.enums.GameType;
import net.dv8tion.jda.api.entities.Member;

import javax.annotation.Nullable;
import java.util.*;

public class MatchController {

    public static final Map<String, Match> matches = new HashMap<>();
    public static final Map<Game, LinkedList<Long>> row = new HashMap<>();
    public static final Map<Long, Game> playersInQueue = new HashMap<>();
    public static final Map<String, Match> pastMatches = new HashMap<>();

    public static void startup() {
        for (GameType type : GameType.values()) {
            for (GameMode mode : GameMode.values()) {
                row.put(new Game(type, mode), new LinkedList<>());
            }
        }
    }

    public static boolean putPlayerInRow(GameType type, GameMode mode, Long userID) {
        if (playersInQueue.containsKey(userID)) return false;
        getByType(type, mode).add(userID);
        playersInQueue.put(userID, new Game(type, mode));

        return true;
    }

    public static void removePlayerFromRow(Long userID) {
        Game game = playersInQueue.get(userID);
        getByType(game.type, game.mode).remove(userID);
        playersInQueue.remove(userID);
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
        row.forEach((game, users) -> {
            if (users.size() >= 1) {
                Match match = new Match(game);

                int rankPosition = 99;
                List<Long> selectedPlayers = new ArrayList<>();
                for (int i = 0; i < Math.min(game.type.minPlayers, users.size()); i++) {
                    Long userID = users.get(i);

                    if (game.mode == GameMode.RANKED) {
                        Player data = PlayerService.INSTANCE.get(userID);
                        int pessoalPosition = game.type == GameType.LUDO ? data.ludoRank.position : data.poolRank.position;

                        if (rankPosition == 99) rankPosition = pessoalPosition;
                        for (i = pessoalPosition - 1; i < pessoalPosition + 1; i++) {
                            if (rankPosition == i) {
                                selectedPlayers.add(userID);
                                users.remove(userID);
                                break;
                            }

                        }
                    } else {
                        selectedPlayers.add(userID);
                        users.remove(userID);
                    }
                }

                match.players = selectedPlayers;
                match.createChannel();
            }
        });
    }
}
