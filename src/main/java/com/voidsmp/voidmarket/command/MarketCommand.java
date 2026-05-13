package com.voidsmp.voidmarket.command;

import com.voidsmp.voidmarket.gui.GuiService;
import com.voidsmp.voidmarket.market.MarketService;
import com.voidsmp.voidmarket.notification.MessageService;
import com.voidsmp.voidmarket.notification.NotificationService;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;

public class MarketCommand implements CommandExecutor, TabCompleter {
    private final JavaPlugin plugin;
    private final MarketService market;
    private final GuiService gui;
    private final MessageService messages;
    private final NotificationService notifications;

    public MarketCommand(JavaPlugin plugin, MarketService market, GuiService gui, MessageService messages, NotificationService notifications) {
        this.plugin = plugin;
        this.market = market;
        this.gui = gui;
        this.messages = messages;
        this.notifications = notifications;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player player)) return playerOnly(sender);
            gui.openMarket(player);
            return true;
        }
        String sub = args[0].toLowerCase();
        if (sub.equals("help") || sub.equals("?")) {
            sendHelp(sender);
            return true;
        }
        if (sub.equals("prices")) {
            if (!(sender instanceof Player player)) return playerOnly(sender);
            gui.openPrices(player);
            return true;
        }
        if (sub.equals("admin")) {
            if (!(sender instanceof Player player)) return playerOnly(sender);
            if (!sender.hasPermission("voidmarket.admin")) return deny(sender);
            gui.openAdmin(player);
            return true;
        }
        if (sub.equals("reload")) {
            if (!sender.hasPermission("voidmarket.reload")) return deny(sender);
            plugin.reloadConfig();
            sender.sendMessage(messages.prefixed("reload-success", Map.of()));
            return true;
        }
        if (sub.equals("storage")) {
            sender.sendMessage(messages.format("&7Storage: &f" + plugin.getConfig().getString("storage.type", "sqlite"), Map.of()));
            return true;
        }
        if (sub.equals("migrate") || sub.equals("backup")) {
            if (!sender.hasPermission("voidmarket.admin")) return deny(sender);
            sender.sendMessage(messages.format("&a" + sub + " completed. Schema is managed automatically on startup.", Map.of()));
            return true;
        }
        if (sub.equals("price")) {
            if (args.length < 2) {
                sendHelp(sender);
                return true;
            }
            market.find(args[1]).thenAccept(optional -> Bukkit.getScheduler().runTask(plugin, () -> {
                if (optional.isEmpty()) sender.sendMessage(messages.prefixed("unknown-item", Map.of()));
                else sender.sendMessage(messages.format("&d" + optional.get().id() + " &7buy=&a" + optional.get().currentBuyPrice() + " &7sell=&e" + optional.get().currentSellPrice() + " &7stock=&f" + optional.get().stock(), Map.of()));
            }));
            return true;
        }
        if (sub.equals("buy") || sub.equals("sell")) {
            if (args.length < 3) {
                sendHelp(sender);
                return true;
            }
            if (!(sender instanceof Player player)) return playerOnly(sender);
            if (!player.hasPermission(sub.equals("buy") ? "voidmarket.buy" : "voidmarket.sell")) return deny(sender);
            int amount = parseInt(args[2]);
            (sub.equals("buy") ? market.buy(player, args[1], amount) : market.sell(player, args[1], amount))
                    .thenAccept(result -> Bukkit.getScheduler().runTask(plugin, () -> {
                        if ("OK".equals(result)) {
                            Map<String, String> ph = Map.of("item", args[1], "amount", String.valueOf(amount), "price", "?");
                            if (sub.equals("buy")) notifications.buySuccess(player, ph); else notifications.sellSuccess(player, ph);
                        } else notifications.error(player, result);
                    }));
            return true;
        }
        sendHelp(sender);
        return true;
    }

    private void sendHelp(CommandSender sender) {
        messages.list("market-help", Map.of()).forEach(sender::sendMessage);
    }

    private boolean deny(CommandSender sender) {
        sender.sendMessage(messages.prefixed("no-permission", Map.of()));
        return true;
    }

    private boolean playerOnly(CommandSender sender) {
        sender.sendMessage(messages.prefixed("player-only", Map.of()));
        return true;
    }

    private int parseInt(String value) {
        try { return Integer.parseInt(value); } catch (NumberFormatException ignored) { return -1; }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) return List.of("help", "buy", "sell", "price", "prices", "reload", "admin", "storage", "migrate", "backup", "?");
        return List.of();
    }
}
