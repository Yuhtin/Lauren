package database.types;

import dao.controller.PlayerDataController;
import database.Data;
import utils.serialization.DataGson;

import java.util.List;

public class SQLite implements Data {


    @Override
    public boolean createTable() {
        return false;
    }

    @Override
    public List<PlayerDataController> loadAll() {
        return null;
    }

    @Override
    public void save(PlayerDataController data) {
        String result = DataGson.serialize(data);

        //System.out.println("INSERT INTO lauren_players (id, data) VALUES(" + data.userID + "," + result + ") ON DUPLICATE KEY UPDATE data='" + result + "'");
    }

    @Override
    public void close() {

    }
}
