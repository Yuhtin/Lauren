package com.yuhtin.lauren.models.cache;

import com.yuhtin.lauren.models.data.Match;
import net.dv8tion.jda.api.entities.Member;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

public class MatchCache {

    public static final Map<String, Match> matches = new LinkedHashMap<>();
    public static final Map<String, Match> pastMatches = new LinkedHashMap<>();

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
}
