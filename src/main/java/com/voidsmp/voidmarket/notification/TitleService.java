package com.voidsmp.voidmarket.notification;

import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Map;

public class TitleService {
    private final MessageService messages;

    public TitleService(MessageService messages) {
        this.messages = messages;
    }

    public void show(Player player, String key, Map<String, String> placeholders) {
        player.showTitle(Title.title(
                messages.component("titles." + key + ".title", placeholders),
                messages.component("titles." + key + ".subtitle", placeholders),
                Title.Times.times(Duration.ofMillis(300), Duration.ofMillis(1600), Duration.ofMillis(400))));
    }
}
