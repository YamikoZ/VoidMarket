package com.voidsmp.voidmarket.util;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public final class ItemSerializer {
    private ItemSerializer() {
    }

    public static String serialize(ItemStack item) {
        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream();
             BukkitObjectOutputStream out = new BukkitObjectOutputStream(bytes)) {
            out.writeObject(item);
            return Base64.getEncoder().encodeToString(bytes.toByteArray());
        } catch (IOException exception) {
            throw new IllegalStateException("Could not serialize item", exception);
        }
    }

    public static ItemStack deserialize(String data) {
        try (ByteArrayInputStream bytes = new ByteArrayInputStream(Base64.getDecoder().decode(data));
             BukkitObjectInputStream in = new BukkitObjectInputStream(bytes)) {
            Object object = in.readObject();
            if (!(object instanceof ItemStack item)) {
                throw new IllegalStateException("Serialized data is not an ItemStack");
            }
            return item;
        } catch (IOException | ClassNotFoundException | IllegalArgumentException exception) {
            throw new IllegalStateException("Could not deserialize item", exception);
        }
    }
}
