package database.types;

import dao.controller.PlayerDataController;
import database.Data;

import java.util.List;

public class MongoDB implements Data {

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

    }

    @Override
    public void close() {

    }
}
