package com.voidsmp.voidmarket.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class MySQLStorageProvider implements StorageProvider {
    private final JavaPlugin plugin;
    private HikariDataSource dataSource;

    public MySQLStorageProvider(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void init() {
        ConfigurationSection mysql = plugin.getConfig().getConfigurationSection("storage.mysql");
        HikariConfig config = new HikariConfig();
        config.setPoolName("VoidMarket-MySQL");
        config.setJdbcUrl("jdbc:mysql://" + mysql.getString("host", "localhost") + ":" + mysql.getInt("port", 3306)
                + "/" + mysql.getString("database", "voidmarket") + "?useSSL=" + mysql.getBoolean("useSSL", false)
                + "&characterEncoding=utf8&useUnicode=true");
        config.setUsername(mysql.getString("username", "root"));
        config.setPassword(mysql.getString("password", ""));
        ConfigurationSection pool = mysql.getConfigurationSection("pool");
        config.setMaximumPoolSize(pool.getInt("maximumPoolSize", 10));
        config.setMinimumIdle(pool.getInt("minimumIdle", 2));
        config.setConnectionTimeout(pool.getLong("connectionTimeout", 30000));
        config.setIdleTimeout(pool.getLong("idleTimeout", 600000));
        config.setMaxLifetime(pool.getLong("maxLifetime", 1800000));
        dataSource = new HikariDataSource(config);
    }

    @Override public Connection connection() throws SQLException { return dataSource.getConnection(); }
    @Override public DataSource dataSource() { return dataSource; }
    @Override public boolean mysql() { return true; }
    @Override public String type() { return "mysql"; }
    @Override public void close() { if (dataSource != null) dataSource.close(); }
}
