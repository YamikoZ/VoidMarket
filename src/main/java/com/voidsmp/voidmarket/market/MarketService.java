package com.voidsmp.voidmarket.market;

import com.voidsmp.voidmarket.config.Settings;
import com.voidsmp.voidmarket.economy.EconomyService;
import com.voidsmp.voidmarket.model.MarketItem;
import com.voidsmp.voidmarket.model.MarketTransaction;
import com.voidsmp.voidmarket.pricing.PricingService;
import com.voidsmp.voidmarket.storage.MarketRepository;
import com.voidsmp.voidmarket.storage.StorageProvider;
import com.voidsmp.voidmarket.storage.TransactionRepository;
import com.voidsmp.voidmarket.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.sql.Connection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class MarketService {
    private final JavaPlugin plugin;
    private final StorageProvider storage;
    private final MarketRepository marketRepository;
    private final TransactionRepository transactionRepository;
    private final PricingService pricingService;
    private final EconomyService economy;
    private final Settings settings;
    private final Executor executor;

    public MarketService(JavaPlugin plugin, StorageProvider storage, MarketRepository marketRepository,
                         TransactionRepository transactionRepository, PricingService pricingService,
                         EconomyService economy, Settings settings, Executor executor) {
        this.plugin = plugin;
        this.storage = storage;
        this.marketRepository = marketRepository;
        this.transactionRepository = transactionRepository;
        this.pricingService = pricingService;
        this.economy = economy;
        this.settings = settings;
        this.executor = executor;
    }

    public void seedFromConfig() {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("market-items");
        if (section == null) return;
        for (String id : section.getKeys(false)) {
            ConfigurationSection item = section.getConfigurationSection(id);
            Material material = Material.matchMaterial(item.getString("material", id).toUpperCase(Locale.ROOT));
            if (material == null || settings.isBlocked(material)) continue;
            double base = item.getDouble("base-price", 1.0);
            int stock = item.getInt("stock", 0);
            int target = item.getInt("target-stock", Math.max(1, stock));
            MarketItem marketItem = new MarketItem(id.toLowerCase(Locale.ROOT), material, item.getString("category", "Special"),
                    item.getString("display-name", material.name()), base, base, base * 0.85, settings.minMultiplier(),
                    settings.maxMultiplier(), stock, target, settings.dailyStockLimit(), 0, 0,
                    com.voidsmp.voidmarket.model.Trend.STABLE, true, System.currentTimeMillis());
            try {
                marketRepository.upsert(marketItem);
            } catch (Exception exception) {
                plugin.getLogger().warning("Could not seed market item " + id + ": " + exception.getMessage());
            }
        }
    }

    public CompletableFuture<List<MarketItem>> all() {
        return CompletableFuture.supplyAsync(() -> {
            try { return marketRepository.findAll(); } catch (Exception e) { throw new RuntimeException(e); }
        }, executor);
    }

    public CompletableFuture<Optional<MarketItem>> find(String id) {
        return CompletableFuture.supplyAsync(() -> {
            try { return marketRepository.find(id); } catch (Exception e) { throw new RuntimeException(e); }
        }, executor);
    }

    public CompletableFuture<String> buy(Player player, String id, int amount) {
        if (amount <= 0) return CompletableFuture.completedFuture("invalid-amount");
        return find(id).thenCompose(optional -> {
            if (optional.isEmpty()) return CompletableFuture.completedFuture("unknown-item");
            MarketItem item = optional.get();
            if (settings.isBlocked(item.material())) return CompletableFuture.completedFuture("item-blocked");
            if (item.stock() < amount) return CompletableFuture.completedFuture("not-enough-stock");
            double total = item.currentBuyPrice() * amount;
            if (!economy.has(player, total)) return CompletableFuture.completedFuture("not-enough-money");
            if (!ItemUtil.hasSpace(player, new ItemStack(item.material()), amount)) return CompletableFuture.completedFuture("inventory-full");
            if (!economy.withdraw(player, total)) return CompletableFuture.completedFuture("not-enough-money");
            return CompletableFuture.supplyAsync(() -> {
                try (Connection c = storage.connection()) {
                    c.setAutoCommit(false);
                    MarketItem traded = item.withPrices(item.currentBuyPrice(), item.currentSellPrice(), item.stock() - amount,
                            item.dailyBuyVolume() + amount, item.dailySellVolume(), item.trend());
                    traded = pricingService.recalculate(traded);
                    marketRepository.updateAfterTrade(c, traded);
                    marketRepository.insertHistory(c, traded);
                    transactionRepository.insert(c, new MarketTransaction("MARKET_BUY", player.getUniqueId(), player.getName(),
                            item.id(), null, amount, item.currentBuyPrice(), total, System.currentTimeMillis()));
                    c.commit();
                    return "OK";
                } catch (Exception exception) {
                    economy.deposit(player, total);
                    throw new RuntimeException(exception);
                }
            }, executor).thenApply(result -> {
                runMain(() -> ItemUtil.give(player, new ItemStack(item.material()), amount));
                return result;
            });
        });
    }

    public CompletableFuture<String> sell(Player player, String id, int amount) {
        if (amount <= 0) return CompletableFuture.completedFuture("invalid-amount");
        return find(id).thenCompose(optional -> {
            if (optional.isEmpty()) return CompletableFuture.completedFuture("unknown-item");
            MarketItem item = optional.get();
            if (settings.isBlocked(item.material())) return CompletableFuture.completedFuture("item-blocked");
            if (item.dailySellVolume() + amount > item.dailyStockLimit()) return CompletableFuture.completedFuture("daily-limit-reached");
            if (ItemUtil.count(player, item.material()) < amount) return CompletableFuture.completedFuture("not-enough-stock");
            double total = item.currentSellPrice() * amount;
            if (!ItemUtil.remove(player, item.material(), amount)) return CompletableFuture.completedFuture("not-enough-stock");
            return CompletableFuture.supplyAsync(() -> {
                try (Connection c = storage.connection()) {
                    c.setAutoCommit(false);
                    MarketItem traded = item.withPrices(item.currentBuyPrice(), item.currentSellPrice(), item.stock() + amount,
                            item.dailyBuyVolume(), item.dailySellVolume() + amount, item.trend());
                    traded = pricingService.recalculate(traded);
                    marketRepository.updateAfterTrade(c, traded);
                    marketRepository.insertHistory(c, traded);
                    transactionRepository.insert(c, new MarketTransaction("MARKET_SELL", player.getUniqueId(), player.getName(),
                            item.id(), null, amount, item.currentSellPrice(), total, System.currentTimeMillis()));
                    c.commit();
                    if (!economy.deposit(player, total)) {
                        runMain(() -> ItemUtil.give(player, new ItemStack(item.material()), amount));
                        return "transaction-cancelled";
                    }
                    return "OK";
                } catch (Exception exception) {
                    runMain(() -> ItemUtil.give(player, new ItemStack(item.material()), amount));
                    throw new RuntimeException(exception);
                }
            }, executor);
        });
    }

    public CompletableFuture<Void> resetDaily() {
        return CompletableFuture.runAsync(() -> {
            try { marketRepository.resetDaily(); } catch (Exception e) { throw new RuntimeException(e); }
        }, executor);
    }

    private void runMain(Runnable task) {
        BukkitScheduler scheduler = plugin.getServer().getScheduler();
        scheduler.runTask(plugin, task);
    }
}
