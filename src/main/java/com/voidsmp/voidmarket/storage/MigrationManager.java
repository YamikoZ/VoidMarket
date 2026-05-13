package com.voidsmp.voidmarket.storage;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class MigrationManager {
    private final StorageProvider storage;

    public MigrationManager(StorageProvider storage) {
        this.storage = storage;
    }

    public void migrate() throws SQLException {
        try (Connection connection = storage.connection(); Statement statement = connection.createStatement()) {
            String auto = storage.mysql() ? "BIGINT AUTO_INCREMENT PRIMARY KEY" : "INTEGER PRIMARY KEY AUTOINCREMENT";
            String bool = storage.mysql() ? "BOOLEAN" : "INTEGER";
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS schema_version (version INTEGER PRIMARY KEY, applied_at BIGINT NOT NULL)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS market_items (" +
                    "id VARCHAR(64) PRIMARY KEY, material VARCHAR(64) NOT NULL, category VARCHAR(64) NOT NULL, display_name VARCHAR(128)," +
                    "base_price DOUBLE NOT NULL, current_buy_price DOUBLE NOT NULL, current_sell_price DOUBLE NOT NULL," +
                    "min_multiplier DOUBLE NOT NULL, max_multiplier DOUBLE NOT NULL, stock INTEGER NOT NULL, target_stock INTEGER NOT NULL," +
                    "daily_stock_limit INTEGER NOT NULL, daily_buy_volume INTEGER NOT NULL DEFAULT 0, daily_sell_volume INTEGER NOT NULL DEFAULT 0," +
                    "trend VARCHAR(16) NOT NULL DEFAULT 'STABLE', enabled " + bool + " NOT NULL DEFAULT 1, updated_at BIGINT NOT NULL)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS market_history (id " + auto + ", item_id VARCHAR(64) NOT NULL," +
                    "buy_price DOUBLE NOT NULL, sell_price DOUBLE NOT NULL, stock INTEGER NOT NULL, buy_volume INTEGER NOT NULL," +
                    "sell_volume INTEGER NOT NULL, trend VARCHAR(16) NOT NULL, created_at BIGINT NOT NULL)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS player_shops (id VARCHAR(36) PRIMARY KEY, owner_uuid VARCHAR(36) NOT NULL," +
                    "owner_name VARCHAR(16) NOT NULL, item_key VARCHAR(128) NOT NULL, serialized_item TEXT NOT NULL, material VARCHAR(64) NOT NULL," +
                    "display_name VARCHAR(128), price DOUBLE NOT NULL, stock INTEGER NOT NULL DEFAULT 0, sold_count INTEGER NOT NULL DEFAULT 0," +
                    "enabled " + bool + " NOT NULL DEFAULT 1, created_at BIGINT NOT NULL, updated_at BIGINT NOT NULL)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS transactions (id " + auto + ", type VARCHAR(32) NOT NULL," +
                    "player_uuid VARCHAR(36) NOT NULL, player_name VARCHAR(16) NOT NULL, item_id VARCHAR(64), shop_id VARCHAR(36)," +
                    "amount INTEGER NOT NULL, unit_price DOUBLE NOT NULL, total_price DOUBLE NOT NULL, created_at BIGINT NOT NULL)");
            statement.executeUpdate((storage.mysql()
                    ? "INSERT IGNORE INTO schema_version(version, applied_at) VALUES (1, " + System.currentTimeMillis() + ")"
                    : "INSERT OR IGNORE INTO schema_version(version, applied_at) VALUES (1, " + System.currentTimeMillis() + ")"));
        }
    }
}
