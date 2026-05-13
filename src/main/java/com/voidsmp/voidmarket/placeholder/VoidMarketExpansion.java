package com.voidsmp.voidmarket.placeholder;

import com.voidsmp.voidmarket.market.MarketService;
import com.voidsmp.voidmarket.shop.ShopService;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class VoidMarketExpansion extends PlaceholderExpansion {
    private final MarketService market;
    private final ShopService shops;

    public VoidMarketExpansion(MarketService market, ShopService shops) {
        this.market = market;
        this.shops = shops;
    }

    @Override public @NotNull String getIdentifier() { return "voidmarket"; }
    @Override public @NotNull String getAuthor() { return "VoidSMP"; }
    @Override public @NotNull String getVersion() { return "1.0.0"; }
    @Override public boolean persist() { return true; }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        try {
            if (params.startsWith("price_")) {
                return market.find(params.substring(6)).join().map(i -> String.valueOf(i.currentBuyPrice())).orElse("0");
            }
            if (params.startsWith("stock_")) {
                return market.find(params.substring(6)).join().map(i -> String.valueOf(i.stock())).orElse("0");
            }
            if (params.startsWith("trend_")) {
                return market.find(params.substring(6)).join().map(i -> i.trend().name()).orElse("STABLE");
            }
            if (params.startsWith("daily_volume_")) {
                return market.find(params.substring(13)).join().map(i -> String.valueOf(i.dailyBuyVolume() + i.dailySellVolume())).orElse("0");
            }
            if (params.equals("shops")) {
                return String.valueOf(shops.all().join().size());
            }
        } catch (Exception ignored) {
        }
        return "";
    }
}
