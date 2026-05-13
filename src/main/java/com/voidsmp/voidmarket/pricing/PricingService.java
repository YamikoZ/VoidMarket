package com.voidsmp.voidmarket.pricing;

import com.voidsmp.voidmarket.config.Settings;
import com.voidsmp.voidmarket.model.MarketItem;
import com.voidsmp.voidmarket.model.Trend;

public class PricingService {
    private final Settings settings;

    public PricingService(Settings settings) {
        this.settings = settings;
    }

    public MarketItem recalculate(MarketItem item) {
        double demandFactor = item.dailyBuyVolume() * settings.demandWeight();
        double supplyFactor = item.dailySellVolume() * settings.supplyWeight();
        double shortage = item.targetStock() <= 0 ? 0 : Math.max(0, item.targetStock() - item.stock()) / (double) item.targetStock();
        double scarcityFactor = shortage * settings.scarcityWeight();
        double multiplier = clamp(1.0 + demandFactor - supplyFactor + scarcityFactor,
                Math.max(settings.minMultiplier(), item.minMultiplier()),
                Math.min(settings.maxMultiplier(), item.maxMultiplier()));
        double buy = round(item.basePrice() * multiplier);
        double sell = round(buy * 0.85);
        Trend trend = buy > item.currentBuyPrice() ? Trend.UP : buy < item.currentBuyPrice() ? Trend.DOWN : Trend.STABLE;
        return item.withPrices(buy, sell, item.stock(), item.dailyBuyVolume(), item.dailySellVolume(), trend);
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
