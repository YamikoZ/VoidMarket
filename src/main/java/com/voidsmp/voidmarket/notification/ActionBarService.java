package com.voidsmp.voidmarket.notification;

import org.bukkit.entity.Player;

import java.util.Map;

public class ActionBarService {
    private final MessageService messages;

    public ActionBarService(MessageService messages) {
        this.messages = messages;
    }

    public void send(Player player, String path, Map<String, String> placeholders) {
        player.sendActionBar(messages.component(path, placeholders));
    }
}
