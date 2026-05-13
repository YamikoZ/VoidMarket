package com.voidsmp.voidmarket.model;

import org.bukkit.Material;

public record MarketItem(
        String id,
        Material material,
        String category,
        String displayName,
        double basePrice,
        double currentBuyPrice,
        double currentSellPrice,
        double minMultiplier,
        double maxMultiplier,
        int stock,
        int targetStock,
        int dailyStockLimit,
        int dailyBuyVolume,
        int dailySellVolume,
        Trend trend,
        boolean enabled,
        long updatedAt
) {
    public MarketItem withPrices(double buy, double sell, int stock, int buyVolume, int sellVolume, Trend trend) {
        return new MarketItem(id, material, category, displayName, basePrice, buy, sell, minMultiplier, maxMultiplier,
                stock, targetStock, dailyStockLimit, buyVolume, sellVolume, trend, enabled, System.currentTimeMillis());
    }
}
