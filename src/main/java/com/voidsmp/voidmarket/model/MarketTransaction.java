package com.voidsmp.voidmarket.model;

import java.util.UUID;

public record MarketTransaction(
        String type,
        UUID playerUuid,
        String playerName,
        String itemId,
        String shopId,
        int amount,
        double unitPrice,
        double totalPrice,
        long createdAt
) {
}
