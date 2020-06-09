package matches.controller;

import matches.Match;
import net.dv8tion.jda.api.entities.Member;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class MatchController {

    public static final List<Match> matches = new ArrayList<>();

    @Nullable
    public static Match getByPlayer(Member member) {
        return matches.stream().filter(match -> match.player1.equals(member.getIdLong()) || match.player2.equals(member.getIdLong())).findFirst().orElse(null);
    }
}
