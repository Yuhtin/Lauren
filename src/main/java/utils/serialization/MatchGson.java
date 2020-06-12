package utils.serialization;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import matches.Match;

@AllArgsConstructor
public class MatchGson {

    public static final Gson GSON = new Gson();

    public static String serialize(Match object) { return GSON.toJson(object); }

    public static Match deserialize(String json) { return GSON.fromJson(json, Match.class); }

}
