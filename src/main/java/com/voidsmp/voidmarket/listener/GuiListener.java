package com.voidsmp.voidmarket.listener;

import com.voidsmp.voidmarket.gui.GuiService;
import com.voidsmp.voidmarket.gui.GuiType;
import com.voidsmp.voidmarket.gui.VoidGuiHolder;
import com.voidsmp.voidmarket.market.MarketService;
import com.voidsmp.voidmarket.notification.NotificationService;
import com.voidsmp.voidmarket.shop.ShopService;
import com.voidsmp.voidmarket.util.ItemUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public class GuiListener implements Listener {
    private final JavaPlugin plugin;
    private final GuiService gui;
    private final MarketService market;
    private final ShopService shops;
    private final NotificationService notifications;

    public GuiListener(JavaPlugin plugin, GuiService gui, MarketService market, ShopService shops, NotificationService notifications) {
        this.plugin = plugin;
        this.gui = gui;
        this.market = market;
        this.shops = shops;
        this.notifications = notifications;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player) || !(event.getInventory().getHolder() instanceof VoidGuiHolder holder)) return;
        event.setCancelled(true);
        ItemStack clicked = event.getCurrentItem();
        if (ItemUtil.isEmpty(clicked)) return;
        if (holder.type() == GuiType.MARKET || holder.type() == GuiType.MARKET_PRICES) {
            String id = clicked.getType().name().toLowerCase();
            if (event.isRightClick()) {
                market.sell(player, id, 1).thenAccept(result -> notifyMarket(player, result, clicked.getType().name(), 1, 0, false));
            } else {
                market.buy(player, id, 1).thenAccept(result -> notifyMarket(player, result, clicked.getType().name(), 1, 0, true));
            }
            player.closeInventory();
            return;
        }
        if (holder.type() == GuiType.PSHOP_MAIN) {
            if (event.getSlot() == 10) gui.openManage(player);
            if (event.getSlot() == 12) gui.openBrowse(player);
            return;
        }
        if (holder.type() == GuiType.PSHOP_BROWSE || holder.type() == GuiType.PSHOP_MANAGE) {
            String shopId = extractShopId(clicked);
            if (shopId == null) return;
            if (holder.type() == GuiType.PSHOP_MANAGE && event.isRightClick()) {
                gui.openStock(player, shopId);
            } else if (holder.type() == GuiType.PSHOP_BROWSE) {
                shops.buy(player, shopId, 1).thenAccept(result -> notifyShop(player, result));
                player.closeInventory();
            }
            return;
        }
        if (holder.type() == GuiType.PSHOP_STOCK && event.getSlot() == 15) {
            int amount = event.isRightClick() ? 16 : 1;
            shops.addStock(player, holder.data(), amount).thenAccept(result -> notifyShop(player, result));
            player.closeInventory();
            return;
        }
        if (holder.type() == GuiType.CONFIRM_CREATE) {
            if (event.getSlot() == 15) {
                player.closeInventory();
                return;
            }
            if (event.getSlot() == 11) {
                double price = Double.parseDouble(holder.data());
                ItemStack hand = player.getInventory().getItemInMainHand().clone();
                shops.create(player, hand, price).thenAccept(result -> {
                    if (result.length() == 36) {
                        org.bukkit.Bukkit.getScheduler().runTask(plugin, () -> {
                            player.getInventory().setItemInMainHand(null);
                            notifications.message(player, "shop-created", Map.of("shop", result));
                        });
                    } else {
                        org.bukkit.Bukkit.getScheduler().runTask(plugin, () -> notifications.error(player, result));
                    }
                });
                player.closeInventory();
            }
        }
    }

    private void notifyMarket(Player player, String result, String item, int amount, double price, boolean buy) {
        if ("OK".equals(result)) {
            Map<String, String> placeholders = Map.of("item", item, "amount", String.valueOf(amount), "price", String.valueOf(price));
            org.bukkit.Bukkit.getScheduler().runTask(plugin, () -> {
                if (buy) notifications.buySuccess(player, placeholders); else notifications.sellSuccess(player, placeholders);
            });
        } else org.bukkit.Bukkit.getScheduler().runTask(plugin, () -> notifications.error(player, result));
    }

    private void notifyShop(Player player, String result) {
        org.bukkit.Bukkit.getScheduler().runTask(plugin, () -> {
            if ("OK".equals(result)) notifications.message(player, "shop-stock-added", Map.of("amount", "1", "stock", "?"));
            else notifications.error(player, result);
        });
    }

    private String extractShopId(ItemStack item) {
        if (!item.hasItemMeta() || item.getItemMeta().lore() == null) return null;
        String line = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
                .serialize(item.getItemMeta().lore().get(0));
        return line.startsWith("Shop: ") ? line.substring(6) : null;
    }
}
