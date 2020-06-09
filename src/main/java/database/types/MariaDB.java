package database.types;

import dao.controller.PlayerDataController;
import database.Data;

public class MariaDB implements Data {
    @Override
    public boolean openConnection() {
        return false;
    }

    @Override
    public boolean createTable() {
        return false;
    }

    @Override
    public boolean loadData() {
        return false;
    }

    @Override
    public void save(Long userID, PlayerDataController controller) {

    }

    @Override
    public void close() {

    }
}
