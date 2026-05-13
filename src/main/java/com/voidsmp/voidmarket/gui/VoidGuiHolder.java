package com.voidsmp.voidmarket.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class VoidGuiHolder implements InventoryHolder {
    private final GuiType type;
    private final String data;
    private Inventory inventory;

    public VoidGuiHolder(GuiType type, String data) {
        this.type = type;
        this.data = data;
    }

    public GuiType type() { return type; }
    public String data() { return data; }
    public void inventory(Inventory inventory) { this.inventory = inventory; }
    @Override public Inventory getInventory() { return inventory; }
}
