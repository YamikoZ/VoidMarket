package com.voidsmp.voidmarket.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.java.JavaPlugin;

import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

public class SQLiteStorageProvider implements StorageProvider {
    private final JavaPlugin plugin;
    private HikariDataSource dataSource;

    public SQLiteStorageProvider(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void init() {
        File file = new File(plugin.getDataFolder(), plugin.getConfig().getString("storage.sqlite.file", "market.db"));
        HikariConfig config = new HikariConfig();
        config.setPoolName("VoidMarket-SQLite");
        config.setJdbcUrl("jdbc:sqlite:" + file.getAbsolutePath());
        config.setMaximumPoolSize(1);
        config.addDataSourceProperty("foreign_keys", "true");
        dataSource = new HikariDataSource(config);
    }

    @Override public Connection connection() throws SQLException { return dataSource.getConnection(); }
    @Override public DataSource dataSource() { return dataSource; }
    @Override public boolean mysql() { return false; }
    @Override public String type() { return "sqlite"; }
    @Override public void close() { if (dataSource != null) dataSource.close(); }
}
