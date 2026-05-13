package com.voidsmp.voidmarket.storage;

import com.voidsmp.voidmarket.model.MarketTransaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TransactionRepository {
    public void insert(Connection c, MarketTransaction tx) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("INSERT INTO transactions(type,player_uuid,player_name,item_id,shop_id,amount,unit_price,total_price,created_at) VALUES (?,?,?,?,?,?,?,?,?)")) {
            ps.setString(1, tx.type());
            ps.setString(2, tx.playerUuid().toString());
            ps.setString(3, tx.playerName());
            ps.setString(4, tx.itemId());
            ps.setString(5, tx.shopId());
            ps.setInt(6, tx.amount());
            ps.setDouble(7, tx.unitPrice());
            ps.setDouble(8, tx.totalPrice());
            ps.setLong(9, tx.createdAt());
            ps.executeUpdate();
        }
    }
}
