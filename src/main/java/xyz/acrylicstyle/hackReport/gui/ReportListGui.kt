package xyz.acrylicstyle.hackReport.gui

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import util.Collection
import util.ICollection
import xyz.acrylicstyle.hackReport.HackReport
import xyz.acrylicstyle.hackReport.HackReport.Companion.instance
import xyz.acrylicstyle.hackReport.utils.PlayerInfo
import xyz.acrylicstyle.hackReport.utils.Utils.getItemStack
import java.util.Arrays
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger

class ReportListGui : InventoryHolder, Listener {
    @Volatile
    private var uuid: UUID? = null
    private val pages = Collection<UUID?, AtomicInteger>()
    fun register(): ReportListGui {
        Bukkit.getPluginManager().registerEvents(this, instance)
        return this
    }

    fun prepare(uuid: UUID?): ReportListGui {
        this.uuid = uuid
        return this
    }

    private fun setItems(): Inventory {
        val page = pages.getOrDefault(uuid, AtomicInteger(1)).get()
        val inventory = Bukkit.createInventory(this, 54, ChatColor.GREEN.toString() + "通報一覧 - ページ" + page)
        HackReport.PLAYERS.filter { p: PlayerInfo -> p.reports != 0 }.forEach { _: UUID?, player: PlayerInfo, index: Int, _: ICollection<UUID?, PlayerInfo?>? ->
            if (index >= 44 * (page - 1) && index <= 44 * page) {
                val skull = ItemStack(Material.SKULL_ITEM, 1, 3.toShort())
                val skullMeta = skull.itemMeta as SkullMeta
                skullMeta.owner = player.name
                skullMeta.displayName = ChatColor.RED.toString() + player.name
                skullMeta.lore = listOf(
                    ChatColor.GREEN.toString() + "UUID: " + ChatColor.GOLD + player.uniqueId.toString(),
                    "",
                    ChatColor.GOLD.toString() + "通報された回数: " + ChatColor.RED + player.reports + ChatColor.GOLD + "回",
                    "",
                    ChatColor.GOLD.toString() + "キル数: " + ChatColor.RED + player.kills + ChatColor.GOLD + "回",
                    ChatColor.GOLD.toString() + "死んだ回数: " + ChatColor.RED + player.deaths + ChatColor.GOLD + "回",
                    "",
                    ChatColor.GOLD.toString() + "BANされている: " + ChatColor.RED + if (Bukkit.getOfflinePlayer(player.uniqueId).isBanned) "はい" else "いいえ")
                skull.itemMeta = skullMeta
                inventory.setItem(index - 44 * (page - 1), skull)
            }
        }
        inventory.setItem(45, getItemStack(Material.ARROW, ChatColor.YELLOW.toString() + "←前のページ"))
        inventory.setItem(49, getItemStack(Material.WATER_BUCKET, ChatColor.YELLOW.toString() + "画面を切り替える"))
        inventory.setItem(53, getItemStack(Material.ARROW, ChatColor.YELLOW.toString() + "次のページ→"))
        return inventory
    }

    override fun getInventory(): Inventory {
        return setItems()
    }

    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
        if (e.inventory.holder !== this) return
        if (e.clickedInventory == null || e.clickedInventory.holder !== this) return
        e.isCancelled = true
        val p = e.whoClicked as Player
        if (e.slot < 45) {
            if (!p.hasPermission("hackreport.player")) {
                p.sendMessage(ChatColor.RED.toString() + "You don't have permission to do this.")
                return
            }
            val page = pages.getOrDefault(uuid, AtomicInteger(1)).get()
            HackReport.PLAYERS.filter { p2: PlayerInfo -> p2.reports != 0 }.foreach { player: PlayerInfo, index: Int ->
                if (index >= 44 * (page - 1) && index <= 44 * page) {
                    if (index - 44 * (page - 1) == e.slot) {
                        p.openInventory(PlayerActionGui().register().prepare(p, player.name, player.uniqueId).inventory)
                    }
                }
            }
            return
        }
        if (e.slot == 45) {
            if (!pages.containsKey(p.uniqueId)) pages.add(p.uniqueId, AtomicInteger(1))
            if (pages[p.uniqueId]!!.get() > 1) pages[p.uniqueId]!!.decrementAndGet()
            p.openInventory(inventory)
        } else if (e.slot == 49) {
            p.openInventory(ReportList2Gui().register().prepare(uuid).inventory)
        } else if (e.slot == 53) {
            if (!pages.containsKey(p.uniqueId)) pages.add(p.uniqueId, AtomicInteger(1))
            pages[p.uniqueId]!!.incrementAndGet()
            p.openInventory(inventory)
        }
    }

    @EventHandler
    fun onInventoryDragEvent(e: InventoryDragEvent) {
        if (e.inventory.holder !== this) return
        e.isCancelled = true
    }

    @EventHandler
    fun onInventoryEvent(e: InventoryEvent) {
        if (e.inventory.holder !== this) return
        try {
            e.javaClass.getMethod("setCancelled", Boolean::class.javaPrimitiveType).invoke(e, true)
        } catch (ignore: ReflectiveOperationException) {
        }
    }

    @EventHandler
    fun onInventoryClose(e: InventoryCloseEvent) {
        pages.remove(e.player.uniqueId) // there is no inventory holder check, intentionally.
    }
}