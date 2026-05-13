package com.voidsmp.voidmarket.storage;

import com.voidsmp.voidmarket.model.MarketItem;
import com.voidsmp.voidmarket.model.Trend;
import org.bukkit.Material;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MarketRepository {
    private final StorageProvider storage;

    public MarketRepository(StorageProvider storage) {
        this.storage = storage;
    }

    public List<MarketItem> findAll() throws SQLException {
        try (Connection c = storage.connection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM market_items WHERE enabled=1 ORDER BY category, id");
             ResultSet rs = ps.executeQuery()) {
            List<MarketItem> items = new ArrayList<>();
            while (rs.next()) items.add(map(rs));
            return items;
        }
    }

    public Optional<MarketItem> find(String id) throws SQLException {
        try (Connection c = storage.connection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM market_items WHERE id=? AND enabled=1")) {
            ps.setString(1, id.toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    public void upsert(MarketItem item) throws SQLException {
        String sql = storage.mysql()
                ? "INSERT INTO market_items VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE material=VALUES(material),category=VALUES(category),display_name=VALUES(display_name),base_price=VALUES(base_price),current_buy_price=VALUES(current_buy_price),current_sell_price=VALUES(current_sell_price),min_multiplier=VALUES(min_multiplier),max_multiplier=VALUES(max_multiplier),stock=VALUES(stock),target_stock=VALUES(target_stock),daily_stock_limit=VALUES(daily_stock_limit),daily_buy_volume=VALUES(daily_buy_volume),daily_sell_volume=VALUES(daily_sell_volume),trend=VALUES(trend),enabled=VALUES(enabled),updated_at=VALUES(updated_at)"
                : "INSERT INTO market_items VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ON CONFLICT(id) DO UPDATE SET material=excluded.material,category=excluded.category,display_name=excluded.display_name,base_price=excluded.base_price,current_buy_price=excluded.current_buy_price,current_sell_price=excluded.current_sell_price,min_multiplier=excluded.min_multiplier,max_multiplier=excluded.max_multiplier,stock=excluded.stock,target_stock=excluded.target_stock,daily_stock_limit=excluded.daily_stock_limit,daily_buy_volume=excluded.daily_buy_volume,daily_sell_volume=excluded.daily_sell_volume,trend=excluded.trend,enabled=excluded.enabled,updated_at=excluded.updated_at";
        try (Connection c = storage.connection(); PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, item);
            ps.executeUpdate();
        }
    }

    public void updateAfterTrade(Connection c, MarketItem item) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("UPDATE market_items SET current_buy_price=?, current_sell_price=?, stock=?, daily_buy_volume=?, daily_sell_volume=?, trend=?, updated_at=? WHERE id=?")) {
            ps.setDouble(1, item.currentBuyPrice());
            ps.setDouble(2, item.currentSellPrice());
            ps.setInt(3, item.stock());
            ps.setInt(4, item.dailyBuyVolume());
            ps.setInt(5, item.dailySellVolume());
            ps.setString(6, item.trend().name());
            ps.setLong(7, System.currentTimeMillis());
            ps.setString(8, item.id());
            ps.executeUpdate();
        }
    }

    public void resetDaily() throws SQLException {
        try (Connection c = storage.connection(); PreparedStatement ps = c.prepareStatement("UPDATE market_items SET daily_buy_volume=0,daily_sell_volume=0,trend='STABLE'")) {
            ps.executeUpdate();
        }
    }

    public void insertHistory(Connection c, MarketItem item) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("INSERT INTO market_history(item_id,buy_price,sell_price,stock,buy_volume,sell_volume,trend,created_at) VALUES (?,?,?,?,?,?,?,?)")) {
            ps.setString(1, item.id());
            ps.setDouble(2, item.currentBuyPrice());
            ps.setDouble(3, item.currentSellPrice());
            ps.setInt(4, item.stock());
            ps.setInt(5, item.dailyBuyVolume());
            ps.setInt(6, item.dailySellVolume());
            ps.setString(7, item.trend().name());
            ps.setLong(8, System.currentTimeMillis());
            ps.executeUpdate();
        }
    }

    private void bind(PreparedStatement ps, MarketItem i) throws SQLException {
        ps.setString(1, i.id()); ps.setString(2, i.material().name()); ps.setString(3, i.category()); ps.setString(4, i.displayName());
        ps.setDouble(5, i.basePrice()); ps.setDouble(6, i.currentBuyPrice()); ps.setDouble(7, i.currentSellPrice());
        ps.setDouble(8, i.minMultiplier()); ps.setDouble(9, i.maxMultiplier()); ps.setInt(10, i.stock()); ps.setInt(11, i.targetStock());
        ps.setInt(12, i.dailyStockLimit()); ps.setInt(13, i.dailyBuyVolume()); ps.setInt(14, i.dailySellVolume());
        ps.setString(15, i.trend().name()); ps.setBoolean(16, i.enabled()); ps.setLong(17, i.updatedAt());
    }

    private MarketItem map(ResultSet rs) throws SQLException {
        return new MarketItem(rs.getString("id"), Material.valueOf(rs.getString("material")), rs.getString("category"),
                rs.getString("display_name"), rs.getDouble("base_price"), rs.getDouble("current_buy_price"),
                rs.getDouble("current_sell_price"), rs.getDouble("min_multiplier"), rs.getDouble("max_multiplier"),
                rs.getInt("stock"), rs.getInt("target_stock"), rs.getInt("daily_stock_limit"), rs.getInt("daily_buy_volume"),
                rs.getInt("daily_sell_volume"), Trend.valueOf(rs.getString("trend")), rs.getBoolean("enabled"), rs.getLong("updated_at"));
    }
}
