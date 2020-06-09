package database;

import dao.controller.PlayerDataController;

import java.sql.Connection;
import java.util.List;

public interface Data {

    Connection connection = null;

    boolean createTable();
    List<PlayerDataController> loadAll();

    void save(PlayerDataController data);
    void close();

}
