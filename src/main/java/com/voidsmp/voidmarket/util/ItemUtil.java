package com.voidsmp.voidmarket.util;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public final class ItemUtil {
    private ItemUtil() {
    }

    public static boolean isEmpty(ItemStack item) {
        return item == null || item.getType().isAir() || item.getAmount() <= 0;
    }

    public static String itemKey(ItemStack item) {
        String name = item.hasItemMeta() && item.getItemMeta().hasDisplayName()
                ? Integer.toHexString(item.getItemMeta().displayName().hashCode())
                : "plain";
        return item.getType().name().toLowerCase(Locale.ROOT) + ":" + name;
    }

    public static int count(Player player, Material material) {
        int count = 0;
        for (ItemStack content : player.getInventory().getContents()) {
            if (!isEmpty(content) && content.getType() == material) {
                count += content.getAmount();
            }
        }
        return count;
    }

    public static boolean remove(Player player, Material material, int amount) {
        if (count(player, material) < amount) {
            return false;
        }
        int remaining = amount;
        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length && remaining > 0; i++) {
            ItemStack stack = contents[i];
            if (isEmpty(stack) || stack.getType() != material) {
                continue;
            }
            int take = Math.min(remaining, stack.getAmount());
            stack.setAmount(stack.getAmount() - take);
            remaining -= take;
            if (stack.getAmount() <= 0) {
                contents[i] = null;
            }
        }
        player.getInventory().setContents(contents);
        return true;
    }

    public static boolean hasSpace(Player player, ItemStack item, int amount) {
        int remaining = amount;
        int max = item.getMaxStackSize();
        for (ItemStack stack : player.getInventory().getStorageContents()) {
            if (isEmpty(stack)) {
                remaining -= max;
            } else if (stack.isSimilar(item)) {
                remaining -= Math.max(0, max - stack.getAmount());
            }
            if (remaining <= 0) {
                return true;
            }
        }
        return false;
    }

    public static void give(Player player, ItemStack item, int amount) {
        int remaining = amount;
        while (remaining > 0) {
            ItemStack clone = item.clone();
            int give = Math.min(remaining, clone.getMaxStackSize());
            clone.setAmount(give);
            player.getInventory().addItem(clone).values().forEach(leftover ->
                    player.getWorld().dropItemNaturally(player.getLocation(), leftover));
            remaining -= give;
        }
    }
}
