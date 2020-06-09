package database.types;

import data.PlayerData;
import database.Data;
import lombok.AllArgsConstructor;

import java.sql.Connection;

@AllArgsConstructor
public class MongoDB implements Data {

    public final Connection connection;
    public final String database, password;

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
