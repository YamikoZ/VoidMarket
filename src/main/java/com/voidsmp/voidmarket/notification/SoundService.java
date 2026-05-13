package com.voidsmp.voidmarket.notification;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundService {
    private final MessageService messages;

    public SoundService(MessageService messages) {
        this.messages = messages;
    }

    public void play(Player player, String key) {
        String name = messages.raw("sounds." + key);
        try {
            if (!name.isBlank()) player.playSound(player.getLocation(), Sound.valueOf(name), 1f, 1f);
        } catch (IllegalArgumentException ignored) {
        }
    }
}
