package xyz.acrylicstyle.hackReport.utils

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.Objects

class InventoryUtils(val inventory: Inventory) {
    fun cloneInventory(): InventoryUtils {
        val newInventory = Bukkit.createInventory(inventory.holder, inventory.size, " ")
        for (i in 0 until inventory.size) newInventory.setItem(i, inventory.getItem(i))
        return InventoryUtils(newInventory)
    }

    fun fillEmptySlots(item: ItemStack?): InventoryUtils {
        val inventory = cloneInventory().inventory
        for (i in 0 until inventory.size) {
            val item2 = inventory.getItem(i)
            if (item2 == null || item2.type == Material.AIR) inventory.setItem(i, item)
        }
        return InventoryUtils(inventory)
    }

    fun fillEmptySlotsWithGlass(): InventoryUtils {
        val blackGlass = ItemStack(Material.STAINED_GLASS_PANE, 1, 15.toShort())
        val meta = Objects.requireNonNull(blackGlass.itemMeta)
        meta.displayName = " "
        blackGlass.itemMeta = meta
        return fillEmptySlots(blackGlass)
    }
}