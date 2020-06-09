package database;

import dao.controller.PlayerDataController;

public interface Data {

    boolean openConnection();
    boolean createTable();
    boolean loadData();
    void save(Long userID, PlayerDataController controller);
    void close();
}
