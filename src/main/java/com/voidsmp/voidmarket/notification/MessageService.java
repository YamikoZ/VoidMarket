package com.voidsmp.voidmarket.notification;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageService {
    private final JavaPlugin plugin;
    private final MiniMessage mini = MiniMessage.miniMessage();
    private final LegacyComponentSerializer legacy = LegacyComponentSerializer.legacyAmpersand();
    private YamlConfiguration messages;

    public MessageService(JavaPlugin plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        plugin.saveResource("messages_en.yml", false);
        plugin.saveResource("messages_th.yml", false);
        String language = plugin.getConfig().getString("language", "en").toLowerCase();
        File file = new File(plugin.getDataFolder(), "messages_" + language + ".yml");
        if (!file.exists()) {
            file = new File(plugin.getDataFolder(), "messages_en.yml");
        }
        messages = YamlConfiguration.loadConfiguration(file);
    }

    public Component component(String path) {
        return component(path, Map.of());
    }

    public Component component(String path, Map<String, String> placeholders) {
        return format(raw(path), placeholders);
    }

    public List<Component> list(String path, Map<String, String> placeholders) {
        return messages.getStringList(path).stream().map(line -> format(line, placeholders)).toList();
    }

    public String raw(String path) {
        return messages.getString(path, path);
    }

    public Component prefixed(String path, Map<String, String> placeholders) {
        Map<String, String> copy = new HashMap<>(placeholders);
        return format(raw("prefix") + raw(path), copy);
    }

    public Component format(String text, Map<String, String> placeholders) {
        String value = text == null ? "" : text;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            value = value.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        value = value.replaceAll("\\{[a-zA-Z0-9_-]+}", "?");
        if (plugin.getConfig().getBoolean("modern-gradients.enabled", true) && value.contains("<")) {
            return mini.deserialize(legacyToMiniMessage(value));
        }
        return legacy.deserialize(value);
    }

    public String resolveReason(String reason, Map<String, String> placeholders) {
        String raw = raw(reason);
        if (raw.equals(reason)) {
            return reason;
        }
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            raw = raw.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return raw.replaceAll("\\{[a-zA-Z0-9_-]+}", "?");
    }

    private String legacyToMiniMessage(String value) {
        String converted = value
                .replace("&0", "<black>")
                .replace("&1", "<dark_blue>")
                .replace("&2", "<dark_green>")
                .replace("&3", "<dark_aqua>")
                .replace("&4", "<dark_red>")
                .replace("&5", "<dark_purple>")
                .replace("&6", "<gold>")
                .replace("&7", "<gray>")
                .replace("&8", "<dark_gray>")
                .replace("&9", "<blue>")
                .replace("&a", "<green>")
                .replace("&b", "<aqua>")
                .replace("&c", "<red>")
                .replace("&d", "<light_purple>")
                .replace("&e", "<yellow>")
                .replace("&f", "<white>")
                .replace("&l", "<bold>")
                .replace("&o", "<italic>")
                .replace("&n", "<underlined>")
                .replace("&m", "<strikethrough>")
                .replace("&k", "<obfuscated>")
                .replace("&r", "<reset>");
        return converted.replaceAll("&\\#([A-Fa-f0-9]{6})", "<#$1>");
    }
}
