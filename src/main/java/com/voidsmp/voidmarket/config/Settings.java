package com.voidsmp.voidmarket.config;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class Settings {
    private final FileConfiguration config;
    private Set<Material> blocked = Set.of();
    private Set<Material> allowed = Set.of();

    public Settings(FileConfiguration config) {
        this.config = config;
        reload();
    }

    public void reload() {
        blocked = parseMaterials("blocked-items");
        allowed = parseMaterials("allowed-items");
    }

    private Set<Material> parseMaterials(String path) {
        Set<Material> set = new HashSet<>();
        for (String raw : config.getStringList(path)) {
            Material material = Material.matchMaterial(raw.toUpperCase(Locale.ROOT));
            if (material != null) {
                set.add(material);
            }
        }
        return set;
    }

    public boolean isBlocked(Material material) {
        return blocked.contains(material) || (!allowed.isEmpty() && !allowed.contains(material));
    }

    public double minMultiplier() { return config.getDouble("min-price-multiplier", 0.5); }
    public double maxMultiplier() { return config.getDouble("max-price-multiplier", 3.0); }
    public double demandWeight() { return config.getDouble("demand-weight", 0.02); }
    public double supplyWeight() { return config.getDouble("supply-weight", 0.02); }
    public double scarcityWeight() { return config.getDouble("scarcity-weight", 0.05); }
    public double taxPercent() { return config.getDouble("tax-percent", 5.0); }
    public int dailyStockLimit() { return config.getInt("daily-stock-limit", 5000); }
    public int maxShopStock() { return config.getInt("max-shop-stock", 3456); }
    public boolean allowBuyOwnShop() { return config.getBoolean("allow-buy-own-shop", false); }

    public int shopLimit(org.bukkit.entity.Player player) {
        if (player.hasPermission("voidmarket.shop.limit.staff")) return config.getInt("max-shops-staff", 50);
        if (player.hasPermission("voidmarket.shop.limit.svip")) return config.getInt("max-shops-svip", 10);
        if (player.hasPermission("voidmarket.shop.limit.vip")) return config.getInt("max-shops-vip", 5);
        return config.getInt("max-shops-default", 2);
    }
}
