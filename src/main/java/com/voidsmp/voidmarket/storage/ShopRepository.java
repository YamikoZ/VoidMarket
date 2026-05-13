package com.voidsmp.voidmarket.storage;

import com.voidsmp.voidmarket.model.PlayerShop;
import com.voidsmp.voidmarket.util.ItemSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ShopRepository {
    private final StorageProvider storage;

    public ShopRepository(StorageProvider storage) {
        this.storage = storage;
    }

    public List<PlayerShop> findAllEnabled() throws SQLException {
        try (Connection c = storage.connection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM player_shops WHERE enabled=1 ORDER BY updated_at DESC");
             ResultSet rs = ps.executeQuery()) {
            List<PlayerShop> shops = new ArrayList<>();
            while (rs.next()) shops.add(map(rs));
            return shops;
        }
    }

    public List<PlayerShop> findByOwner(UUID owner) throws SQLException {
        try (Connection c = storage.connection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM player_shops WHERE owner_uuid=? AND enabled=1 ORDER BY updated_at DESC")) {
            ps.setString(1, owner.toString());
            try (ResultSet rs = ps.executeQuery()) {
                List<PlayerShop> shops = new ArrayList<>();
                while (rs.next()) shops.add(map(rs));
                return shops;
            }
        }
    }

    public Optional<PlayerShop> find(String id) throws SQLException {
        try (Connection c = storage.connection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM player_shops WHERE id=? AND enabled=1")) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    public int countByOwner(UUID owner) throws SQLException {
        try (Connection c = storage.connection();
             PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM player_shops WHERE owner_uuid=? AND enabled=1")) {
            ps.setString(1, owner.toString());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public void create(PlayerShop shop) throws SQLException {
        try (Connection c = storage.connection();
             PreparedStatement ps = c.prepareStatement("INSERT INTO player_shops(id,owner_uuid,owner_name,item_key,serialized_item,material,display_name,price,stock,sold_count,enabled,created_at,updated_at) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)")) {
            bind(ps, shop);
            ps.executeUpdate();
        }
    }

    public void updateStock(Connection c, String id, int stock, int soldDelta) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("UPDATE player_shops SET stock=?, sold_count=sold_count+?, updated_at=? WHERE id=? AND enabled=1")) {
            ps.setInt(1, stock);
            ps.setInt(2, soldDelta);
            ps.setLong(3, System.currentTimeMillis());
            ps.setString(4, id);
            ps.executeUpdate();
        }
    }

    public void setPrice(String id, double price) throws SQLException {
        try (Connection c = storage.connection();
             PreparedStatement ps = c.prepareStatement("UPDATE player_shops SET price=?, updated_at=? WHERE id=? AND enabled=1")) {
            ps.setDouble(1, price);
            ps.setLong(2, System.currentTimeMillis());
            ps.setString(3, id);
            ps.executeUpdate();
        }
    }

    public void disable(String id, UUID owner, boolean bypass) throws SQLException {
        try (Connection c = storage.connection();
             PreparedStatement ps = c.prepareStatement("UPDATE player_shops SET enabled=0, updated_at=? WHERE id=? " + (bypass ? "" : "AND owner_uuid=?"))) {
            ps.setLong(1, System.currentTimeMillis());
            ps.setString(2, id);
            if (!bypass) ps.setString(3, owner.toString());
            ps.executeUpdate();
        }
    }

    private void bind(PreparedStatement ps, PlayerShop s) throws SQLException {
        ps.setString(1, s.id()); ps.setString(2, s.ownerUuid().toString()); ps.setString(3, s.ownerName());
        ps.setString(4, s.itemKey()); ps.setString(5, ItemSerializer.serialize(s.item())); ps.setString(6, s.material().name());
        ps.setString(7, s.displayName()); ps.setDouble(8, s.price()); ps.setInt(9, s.stock()); ps.setInt(10, s.soldCount());
        ps.setBoolean(11, s.enabled()); ps.setLong(12, s.createdAt()); ps.setLong(13, s.updatedAt());
    }

    private PlayerShop map(ResultSet rs) throws SQLException {
        ItemStack item = ItemSerializer.deserialize(rs.getString("serialized_item"));
        return new PlayerShop(rs.getString("id"), UUID.fromString(rs.getString("owner_uuid")), rs.getString("owner_name"),
                rs.getString("item_key"), item, Material.valueOf(rs.getString("material")), rs.getString("display_name"),
                rs.getDouble("price"), rs.getInt("stock"), rs.getInt("sold_count"), rs.getBoolean("enabled"),
                rs.getLong("created_at"), rs.getLong("updated_at"));
    }
}
