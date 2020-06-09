package database;

import data.PlayerData;

public interface Data {

    boolean openConnection();
    boolean createTable();
    boolean loadData();
    void save(Long userID, PlayerData controller);
    void create(Long userID);
    void close();
}
