package com.yuhtin.lauren.database;

import javax.annotation.Nullable;
import java.sql.Connection;

public interface Data {

    @Nullable Connection openConnection();
}
