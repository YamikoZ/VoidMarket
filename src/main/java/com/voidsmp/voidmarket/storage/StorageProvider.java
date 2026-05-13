package com.voidsmp.voidmarket.storage;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public interface StorageProvider extends AutoCloseable {
    void init() throws SQLException;
    Connection connection() throws SQLException;
    DataSource dataSource();
    boolean mysql();
    String type();
    @Override void close();
}
