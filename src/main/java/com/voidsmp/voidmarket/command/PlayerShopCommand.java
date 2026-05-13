package com.voidsmp.voidmarket.command;

import com.voidsmp.voidmarket.gui.GuiService;
import com.voidsmp.voidmarket.notification.MessageService;
import com.voidsmp.voidmarket.notification.NotificationService;
import com.voidsmp.voidmarket.shop.ShopService;
import com.voidsmp.voidmarket.util.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;

public class PlayerShopCommand implements CommandExecutor, TabCompleter {
    private final JavaPlugin plugin;
    private final ShopService shops;
    private final GuiService gui;
    private final MessageService messages;
    private final NotificationService notifications;

    public PlayerShopCommand(JavaPlugin plugin, ShopService shops, GuiService gui, MessageService messages, NotificationService notifications) {
        this.plugin = plugin;
        this.shops = shops;
        this.gui = gui;
        this.messages = messages;
        this.notifications = notifications;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && (args[0].equalsIgnoreCase("help") || args[0].equals("?"))) {
            sendHelp(sender);
            return true;
        }
        if (!(sender instanceof Player player)) return playerOnly(sender);
        if (args.length == 0) {
            gui.openPShopMain(player);
            return true;
        }
        String sub = args[0].toLowerCase();
        if (sub.equals("browse")) { gui.openBrowse(player); return true; }
        if (sub.equals("list") || sub.equals("manage")) { gui.openManage(player); return true; }
        if (sub.equals("stock")) {
            if (args.length < 2) {
                sendHelp(player);
                return true;
            }
            gui.openStock(player, args[1]);
            return true;
        }
        if (sub.equals("create")) {
            if (args.length < 2) {
                sendHelp(player);
                return true;
            }
            if (!player.hasPermission("voidmarket.shop.create")) return deny(player);
            if (ItemUtil.isEmpty(player.getInventory().getItemInMainHand())) {
                notifications.error(player, "item-blocked");
                return true;
            }
            double price = parseDouble(args[1]);
            if (price <= 0) {
                notifications.error(player, "invalid-amount");
                return true;
            }
            gui.openConfirmCreate(player, price);
            return true;
        }
        if (sub.equals("remove")) {
            if (args.length < 2) {
                sendHelp(player);
                return true;
            }
            if (!player.hasPermission("voidmarket.shop.remove")) return deny(player);
            shops.remove(player, args[1]).thenAccept(result -> Bukkit.getScheduler().runTask(plugin, () -> {
                if ("OK".equals(result)) notifications.message(player, "shop-removed", Map.of());
                else notifications.error(player, result);
            }));
            return true;
        }
        if (sub.equals("setprice")) {
            if (args.length < 3) {
                sendHelp(player);
                return true;
            }
            double price = parseDouble(args[2]);
            shops.setPrice(player, args[1], price).thenAccept(result -> Bukkit.getScheduler().runTask(plugin, () -> {
                if ("OK".equals(result)) notifications.message(player, "shop-price-updated", Map.of("price", String.valueOf(price)));
                else notifications.error(player, result);
            }));
            return true;
        }
        sendHelp(player);
        return true;
    }

    private void sendHelp(CommandSender sender) {
        messages.list("pshop-help", Map.of()).forEach(sender::sendMessage);
        messages.list("shop-help", Map.of()).forEach(sender::sendMessage);
    }

    private boolean deny(Player player) {
        player.sendMessage(messages.prefixed("no-permission", Map.of()));
        return true;
    }

    private boolean playerOnly(CommandSender sender) {
        sender.sendMessage(messages.prefixed("player-only", Map.of()));
        return true;
    }

    private double parseDouble(String value) {
        try { return Double.parseDouble(value); } catch (NumberFormatException ignored) { return -1; }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) return List.of("help", "create", "browse", "list", "manage", "remove", "setprice", "stock", "?");
        return List.of();
    }
}
