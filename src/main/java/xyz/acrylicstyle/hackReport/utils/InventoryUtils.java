package xyz.acrylicstyle.hackReport.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

public class InventoryUtils {
    private final Inventory inventory;

    public InventoryUtils(Inventory inventory) {
        this.inventory = inventory;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public InventoryUtils cloneInventory() {
        Inventory newInventory = Bukkit.createInventory(inventory.getHolder(), inventory.getSize(), inventory.getTitle());
        for (int i = 0; i < inventory.getSize(); i++) newInventory.setItem(i, inventory.getItem(i));
        return new InventoryUtils(newInventory);
    }

    public InventoryUtils fillEmptySlots(ItemStack item) {
        Inventory inventory = cloneInventory().getInventory();
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item2 = inventory.getItem(i);
            if (item2 == null || item2.getType() == Material.AIR) inventory.setItem(i, item);
        }
        return new InventoryUtils(inventory);
    }

    public InventoryUtils fillEmptySlotsWithGlass() {
        ItemStack blackGlass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        ItemMeta meta = Objects.requireNonNull(blackGlass.getItemMeta());
        meta.setDisplayName(" ");
        blackGlass.setItemMeta(meta);
        return fillEmptySlots(blackGlass);
    }
}
