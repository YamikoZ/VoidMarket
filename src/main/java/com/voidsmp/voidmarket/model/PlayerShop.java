package com.voidsmp.voidmarket.model;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public record PlayerShop(
        String id,
        UUID ownerUuid,
        String ownerName,
        String itemKey,
        ItemStack item,
        Material material,
        String displayName,
        double price,
        int stock,
        int soldCount,
        boolean enabled,
        long createdAt,
        long updatedAt
) {
    public PlayerShop withStock(int stock) {
        return new PlayerShop(id, ownerUuid, ownerName, itemKey, item, material, displayName, price, stock,
                soldCount, enabled, createdAt, System.currentTimeMillis());
    }

    public PlayerShop withPrice(double price) {
        return new PlayerShop(id, ownerUuid, ownerName, itemKey, item, material, displayName, price, stock,
                soldCount, enabled, createdAt, System.currentTimeMillis());
    }
}
