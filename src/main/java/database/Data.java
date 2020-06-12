package database;

import data.PlayerData;
import matches.Match;

public interface Data {

    boolean openConnection();
    boolean createTable();
    boolean loadData();
    void save(Long userID, PlayerData controller);
    void save(String id, Match match);
    void create(Long userID);
    void create(String id);
    void close();

}
