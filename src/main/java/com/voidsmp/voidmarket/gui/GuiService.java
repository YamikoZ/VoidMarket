package com.voidsmp.voidmarket.gui;

import com.voidsmp.voidmarket.market.MarketService;
import com.voidsmp.voidmarket.model.MarketItem;
import com.voidsmp.voidmarket.model.PlayerShop;
import com.voidsmp.voidmarket.notification.MessageService;
import com.voidsmp.voidmarket.shop.ShopService;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GuiService {
    private final JavaPlugin plugin;
    private final MessageService messages;
    private final MarketService market;
    private final ShopService shops;

    public GuiService(JavaPlugin plugin, MessageService messages, MarketService market, ShopService shops) {
        this.plugin = plugin;
        this.messages = messages;
        this.market = market;
        this.shops = shops;
    }

    public void openMarket(Player player) {
        market.all().thenAccept(items -> runMain(() -> {
            Inventory inv = inventory(GuiType.MARKET, "", 54, "gui.market-title");
            int slot = 0;
            for (MarketItem item : items) {
                if (slot >= 45) break;
                inv.setItem(slot++, marketIcon(item));
            }
            player.openInventory(inv);
        }));
    }

    public void openPrices(Player player) {
        market.all().thenAccept(items -> runMain(() -> {
            Inventory inv = inventory(GuiType.MARKET_PRICES, "", 54, "gui.prices-title");
            int slot = 0;
            for (MarketItem item : items) {
                if (slot >= 54) break;
                inv.setItem(slot++, marketIcon(item));
            }
            player.openInventory(inv);
        }));
    }

    public void openPShopMain(Player player) {
        Inventory inv = inventory(GuiType.PSHOP_MAIN, "", 27, "gui.pshop-title");
        inv.setItem(10, button(Material.CHEST, "gui.manage-title", List.of("Click to manage your virtual shops")));
        inv.setItem(12, button(Material.EMERALD, "gui.browse-title", List.of("Browse offline-capable player shops")));
        inv.setItem(14, button(Material.ANVIL, "shop-help", List.of("Hold an item and use /pshop create <price>")));
        inv.setItem(16, button(Material.BOOK, "gui.prices-title", List.of("View sale history from transactions table")));
        player.openInventory(inv);
    }

    public void openBrowse(Player player) {
        shops.all().thenAccept(list -> runMain(() -> {
            Inventory inv = inventory(GuiType.PSHOP_BROWSE, "", 54, "gui.browse-title");
            int slot = 0;
            for (PlayerShop shop : list) {
                if (slot >= 54) break;
                inv.setItem(slot++, shopIcon(shop));
            }
            player.openInventory(inv);
        }));
    }

    public void openManage(Player player) {
        shops.owner(player.getUniqueId()).thenAccept(list -> runMain(() -> {
            Inventory inv = inventory(GuiType.PSHOP_MANAGE, "", 54, "gui.manage-title");
            int slot = 0;
            for (PlayerShop shop : list) {
                if (slot >= 54) break;
                inv.setItem(slot++, shopIcon(shop));
            }
            player.openInventory(inv);
        }));
    }

    public void openStock(Player player, String shopId) {
        shops.find(shopId).thenAccept(optional -> runMain(() -> {
            if (optional.isEmpty()) return;
            Inventory inv = inventory(GuiType.PSHOP_STOCK, shopId, 27, "gui.stock-title");
            PlayerShop shop = optional.get();
            inv.setItem(11, shopIcon(shop));
            inv.setItem(15, button(Material.HOPPER, "gui.stock", List.of("Left click: add 1", "Right click: add 16")));
            player.openInventory(inv);
        }));
    }

    public void openConfirmCreate(Player player, double price) {
        Inventory inv = inventory(GuiType.CONFIRM_CREATE, String.valueOf(price), 27, "gui.confirm-title");
        inv.setItem(11, button(Material.LIME_CONCRETE, "gui.confirm", List.of("Create virtual shop")));
        inv.setItem(15, button(Material.RED_CONCRETE, "gui.cancel", List.of("Cancel transaction")));
        player.openInventory(inv);
    }

    public void openAdmin(Player player) {
        Inventory inv = inventory(GuiType.MARKET_ADMIN, "", 27, "gui.admin-title");
        inv.setItem(10, button(Material.CLOCK, "admin-reset-success", List.of("Reset daily demand and supply")));
        inv.setItem(12, button(Material.ANVIL, "admin-rebalance-success", List.of("Force price rebalance")));
        inv.setItem(14, button(Material.BOOK, "gui.prices-title", List.of("Storage: " + plugin.getConfig().getString("storage.type"))));
        player.openInventory(inv);
    }

    private Inventory inventory(GuiType type, String data, int size, String titlePath) {
        VoidGuiHolder holder = new VoidGuiHolder(type, data);
        Inventory inv = Bukkit.createInventory(holder, size, messages.component(titlePath));
        holder.inventory(inv);
        return inv;
    }

    private ItemStack marketIcon(MarketItem item) {
        ItemStack stack = new ItemStack(item.material());
        ItemMeta meta = stack.getItemMeta();
        meta.displayName(messages.format(item.displayName(), Map.of()));
        meta.lore(List.of(
                messages.format("&7ID: &f" + item.id(), Map.of()),
                messages.format("&7Buy: &a" + item.currentBuyPrice(), Map.of()),
                messages.format("&7Sell: &e" + item.currentSellPrice(), Map.of()),
                messages.format("&7Stock: &f" + item.stock(), Map.of()),
                messages.format("&7Demand today: &d" + item.dailyBuyVolume(), Map.of()),
                messages.format("&7Supply today: &b" + item.dailySellVolume(), Map.of()),
                messages.format("&7Trend: &f" + item.trend(), Map.of()),
                messages.format("&8Left click buy 1, right click sell 1", Map.of())));
        stack.setItemMeta(meta);
        return stack;
    }

    private ItemStack shopIcon(PlayerShop shop) {
        ItemStack stack = shop.item().clone();
        stack.setAmount(Math.max(1, Math.min(64, shop.stock())));
        ItemMeta meta = stack.getItemMeta();
        meta.lore(List.of(
                messages.format("&7Shop: &f" + shop.id(), Map.of()),
                messages.format("&7Owner: &f" + shop.ownerName(), Map.of()),
                messages.format("&7Price: &a" + shop.price(), Map.of()),
                messages.format("&7Stock: &f" + shop.stock(), Map.of()),
                messages.format("&7Sold: &d" + shop.soldCount(), Map.of()),
                messages.format("&8Left click buy/manage, right click stock", Map.of())));
        stack.setItemMeta(meta);
        return stack;
    }

    private ItemStack button(Material material, String namePath, List<String> lore) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        Component name = namePath.equals("shop-help") ? messages.format("&dShop Help", Map.of()) : messages.component(namePath);
        meta.displayName(name);
        List<Component> lines = new ArrayList<>();
        for (String line : lore) lines.add(messages.format("&7" + line, Map.of()));
        meta.lore(lines);
        stack.setItemMeta(meta);
        return stack;
    }

    private void runMain(Runnable runnable) {
        Bukkit.getScheduler().runTask(plugin, runnable);
    }
}
