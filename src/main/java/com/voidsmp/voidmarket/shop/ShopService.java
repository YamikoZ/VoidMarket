package com.voidsmp.voidmarket.shop;

import com.voidsmp.voidmarket.config.Settings;
import com.voidsmp.voidmarket.economy.EconomyService;
import com.voidsmp.voidmarket.model.MarketTransaction;
import com.voidsmp.voidmarket.model.PlayerShop;
import com.voidsmp.voidmarket.storage.ShopRepository;
import com.voidsmp.voidmarket.storage.StorageProvider;
import com.voidsmp.voidmarket.storage.TransactionRepository;
import com.voidsmp.voidmarket.util.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ShopService {
    private final JavaPlugin plugin;
    private final StorageProvider storage;
    private final ShopRepository shops;
    private final TransactionRepository transactions;
    private final EconomyService economy;
    private final Settings settings;
    private final Executor executor;

    public ShopService(JavaPlugin plugin, StorageProvider storage, ShopRepository shops, TransactionRepository transactions,
                       EconomyService economy, Settings settings, Executor executor) {
        this.plugin = plugin;
        this.storage = storage;
        this.shops = shops;
        this.transactions = transactions;
        this.economy = economy;
        this.settings = settings;
        this.executor = executor;
    }

    public CompletableFuture<List<PlayerShop>> all() {
        return CompletableFuture.supplyAsync(() -> {
            try { return shops.findAllEnabled(); } catch (Exception e) { throw new RuntimeException(e); }
        }, executor);
    }

    public CompletableFuture<List<PlayerShop>> owner(UUID owner) {
        return CompletableFuture.supplyAsync(() -> {
            try { return shops.findByOwner(owner); } catch (Exception e) { throw new RuntimeException(e); }
        }, executor);
    }

    public CompletableFuture<Optional<PlayerShop>> find(String id) {
        return CompletableFuture.supplyAsync(() -> {
            try { return shops.find(id); } catch (Exception e) { throw new RuntimeException(e); }
        }, executor);
    }

    public CompletableFuture<String> create(Player player, ItemStack hand, double price) {
        if (price <= 0) return CompletableFuture.completedFuture("invalid-amount");
        if (ItemUtil.isEmpty(hand)) return CompletableFuture.completedFuture("item-blocked");
        if (settings.isBlocked(hand.getType())) return CompletableFuture.completedFuture("item-blocked");
        ItemStack one = hand.clone();
        int amount = hand.getAmount();
        one.setAmount(1);
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (shops.countByOwner(player.getUniqueId()) >= settings.shopLimit(player)) {
                    return "no-permission";
                }
                String id = UUID.randomUUID().toString();
                String display = hand.hasItemMeta() && hand.getItemMeta().hasDisplayName() ? hand.getItemMeta().getDisplayName() : hand.getType().name();
                shops.create(new PlayerShop(id, player.getUniqueId(), player.getName(), ItemUtil.itemKey(one), one, one.getType(), display,
                        price, amount, 0, true, System.currentTimeMillis(), System.currentTimeMillis()));
                return id;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, executor);
    }

    public CompletableFuture<String> buy(Player buyer, String shopId, int amount) {
        if (amount <= 0) return CompletableFuture.completedFuture("invalid-amount");
        return find(shopId).thenCompose(optional -> {
            if (optional.isEmpty()) return CompletableFuture.completedFuture("shop-out-of-stock");
            PlayerShop shop = optional.get();
            if (!settings.allowBuyOwnShop() && shop.ownerUuid().equals(buyer.getUniqueId())) return CompletableFuture.completedFuture("transaction-cancelled");
            if (shop.stock() < amount) return CompletableFuture.completedFuture("shop-out-of-stock");
            if (!ItemUtil.hasSpace(buyer, shop.item(), amount)) return CompletableFuture.completedFuture("inventory-full");
            double total = shop.price() * amount;
            double tax = total * (settings.taxPercent() / 100.0);
            double ownerPay = total - tax;
            if (!economy.has(buyer, total)) return CompletableFuture.completedFuture("not-enough-money");
            if (!economy.withdraw(buyer, total)) return CompletableFuture.completedFuture("not-enough-money");
            OfflinePlayer owner = Bukkit.getOfflinePlayer(shop.ownerUuid());
            if (!economy.deposit(owner, ownerPay)) {
                economy.deposit(buyer, total);
                return CompletableFuture.completedFuture("transaction-cancelled");
            }
            return CompletableFuture.supplyAsync(() -> {
                try (Connection c = storage.connection()) {
                    c.setAutoCommit(false);
                    shops.updateStock(c, shop.id(), shop.stock() - amount, amount);
                    transactions.insert(c, new MarketTransaction("PLAYER_SHOP_BUY", buyer.getUniqueId(), buyer.getName(),
                            null, shop.id(), amount, shop.price(), total, System.currentTimeMillis()));
                    c.commit();
                    Bukkit.getScheduler().runTask(plugin, () -> ItemUtil.give(buyer, shop.item(), amount));
                    return "OK";
                } catch (Exception e) {
                    economy.deposit(buyer, total);
                    economy.withdraw(owner, ownerPay);
                    throw new RuntimeException(e);
                }
            }, executor);
        });
    }

    public CompletableFuture<String> setPrice(Player player, String id, double price) {
        if (price <= 0) return CompletableFuture.completedFuture("invalid-amount");
        return find(id).thenCompose(optional -> {
            if (optional.isEmpty() || (!optional.get().ownerUuid().equals(player.getUniqueId()) && !player.hasPermission("voidmarket.bypass"))) {
                return CompletableFuture.completedFuture("no-permission");
            }
            return CompletableFuture.supplyAsync(() -> {
                try { shops.setPrice(id, price); return "OK"; } catch (Exception e) { throw new RuntimeException(e); }
            }, executor);
        });
    }

    public CompletableFuture<String> remove(Player player, String id) {
        return find(id).thenCompose(optional -> {
            if (optional.isEmpty()) return CompletableFuture.completedFuture("shop-out-of-stock");
            PlayerShop shop = optional.get();
            boolean bypass = player.hasPermission("voidmarket.bypass");
            if (!shop.ownerUuid().equals(player.getUniqueId()) && !bypass) return CompletableFuture.completedFuture("no-permission");
            if (!ItemUtil.hasSpace(player, shop.item(), shop.stock())) return CompletableFuture.completedFuture("inventory-full");
            return CompletableFuture.supplyAsync(() -> {
                try {
                    shops.disable(id, player.getUniqueId(), bypass);
                    Bukkit.getScheduler().runTask(plugin, () -> ItemUtil.give(player, shop.item(), shop.stock()));
                    return "OK";
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }, executor);
        });
    }

    public CompletableFuture<String> addStock(Player player, String id, int amount) {
        return find(id).thenCompose(optional -> {
            if (optional.isEmpty() || !optional.get().ownerUuid().equals(player.getUniqueId())) return CompletableFuture.completedFuture("no-permission");
            PlayerShop shop = optional.get();
            if (shop.stock() + amount > settings.maxShopStock()) return CompletableFuture.completedFuture("daily-limit-reached");
            if (ItemUtil.count(player, shop.material()) < amount) return CompletableFuture.completedFuture("not-enough-stock");
            ItemUtil.remove(player, shop.material(), amount);
            return CompletableFuture.supplyAsync(() -> {
                try (Connection c = storage.connection()) {
                    shops.updateStock(c, id, shop.stock() + amount, 0);
                    return "OK";
                } catch (Exception e) {
                    Bukkit.getScheduler().runTask(plugin, () -> ItemUtil.give(player, shop.item(), amount));
                    throw new RuntimeException(e);
                }
            }, executor);
        });
    }
}
