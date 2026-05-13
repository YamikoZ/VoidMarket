package com.voidsmp.voidmarket.notification;

import org.bukkit.entity.Player;

import java.util.Map;

public class NotificationService {
    private final MessageService messages;
    private final TitleService titles;
    private final ActionBarService actionBars;
    private final SoundService sounds;

    public NotificationService(MessageService messages, TitleService titles, ActionBarService actionBars, SoundService sounds) {
        this.messages = messages;
        this.titles = titles;
        this.actionBars = actionBars;
        this.sounds = sounds;
    }

    public void message(Player player, String key, Map<String, String> placeholders) {
        player.sendMessage(messages.prefixed(key, placeholders));
    }

    public void buySuccess(Player player, Map<String, String> placeholders) {
        message(player, "market-buy-success", placeholders);
        titles.show(player, "buy-success", placeholders);
        sounds.play(player, "buy-success");
    }

    public void sellSuccess(Player player, Map<String, String> placeholders) {
        message(player, "market-sell-success", placeholders);
        actionBars.send(player, "market-sell-success", placeholders);
        sounds.play(player, "sell-success");
    }

    public void error(Player player, String reason) {
        String resolved = messages.resolveReason(reason, Map.of());
        Map<String, String> placeholders = Map.of("reason", resolved);
        player.sendMessage(messages.prefixed("transaction-cancelled", placeholders));
        titles.show(player, "error", placeholders);
        sounds.play(player, "error");
    }
}
