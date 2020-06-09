package database.types;

import data.PlayerData;
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
    public void save(Long userID, PlayerData controller) {

    }

    @Override
    public void create(Long userID) {

    }

    @Override
    public void close() {

    }
}
