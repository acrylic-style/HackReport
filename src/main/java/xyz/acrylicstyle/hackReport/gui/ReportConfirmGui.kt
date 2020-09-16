package xyz.acrylicstyle.hackReport.gui

import org.apache.commons.lang.Validate
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import xyz.acrylicstyle.hackReport.HackReport.Companion.getPlayerInfo
import xyz.acrylicstyle.hackReport.HackReport.Companion.instance
import xyz.acrylicstyle.hackReport.utils.InventoryUtils
import xyz.acrylicstyle.hackReport.utils.Utils.onlinePlayers
import xyz.acrylicstyle.hackReport.utils.Utils.webhook
import xyz.acrylicstyle.hackReport.utils.Webhook
import xyz.acrylicstyle.tomeito_api.sounds.Sound
import java.awt.Color
import java.util.function.Consumer

class ReportConfirmGui : InventoryHolder, Listener {
    @Volatile
    private var uuid: Player? = null

    @Volatile
    private var target: Player? = null
    fun register(): ReportConfirmGui {
        Bukkit.getPluginManager().registerEvents(this, instance)
        return this
    }

    fun prepare(reporter: Player, target: Player): ReportConfirmGui {
        uuid = reporter
        this.target = target
        return this
    }

    private val items: Inventory
        get() {
            if (uuid == null || target == null) throw NullPointerException("You must call #prepare first.")
            val inventory = Bukkit.createInventory(this, 27, ChatColor.YELLOW.toString() + "確認")
            val green = ItemStack(Material.WOOL, 1, 5.toShort())
            val greenMeta = green.itemMeta
            Validate.notNull(greenMeta, "Meta cannot be null")
            greenMeta.displayName = ChatColor.RED.toString() + "通報する"
            greenMeta.lore = listOf(ChatColor.RED.toString() + "注意: 虚偽の通報をすると通報権限の剥奪や処罰が行われる可能性があります。")
            green.itemMeta = greenMeta
            val red = ItemStack(Material.WOOL, 1, 14.toShort())
            val redMeta = red.itemMeta
            Validate.notNull(redMeta, "Meta cannot be null")
            redMeta.displayName = ChatColor.RED.toString() + "キャンセル"
            red.itemMeta = redMeta
            inventory.setItem(11, green)
            inventory.setItem(15, red)
            return InventoryUtils(inventory).fillEmptySlotsWithGlass().inventory
        }

    override fun getInventory(): Inventory {
        return items
    }

    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
        if (e.inventory.holder !== this) return
        if (e.clickedInventory == null || e.clickedInventory.holder !== this) return
        e.isCancelled = true
        val player = e.whoClicked as Player
        if (e.slot == 11) {
            onlinePlayers.filter { obj: Player -> obj.isOp }.forEach(Consumer { p: Player ->
                p.playSound(p.location, Sound.BLOCK_NOTE_PLING, 100f, 0f)
                p.sendMessage(ChatColor.GREEN.toString() + "通報: " + ChatColor.RED + target!!.name + ChatColor.GREEN + " from " + ChatColor.YELLOW + player.name)
            })
            getPlayerInfo(target!!.name, target!!.uniqueId).increaseReports()
            player.playSound(player.location, Sound.BLOCK_NOTE_PLING, 100f, 2f)
            player.sendMessage(ChatColor.GREEN.toString() + "通報が完了しました。")
            val webhook = webhook ?: return
            Thread {
                webhook.addEmbed(Webhook.EmbedObject().apply { title = "通報: `${target!!.name}` (from `${player.name}`)" }.apply { color = Color.YELLOW })
                webhook.execute()
            }.start()
            player.closeInventory()
        } else if (e.slot == 15) {
            e.whoClicked.openInventory(ReportGui().register().prepare(e.whoClicked.uniqueId).inventory)
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
        } catch (ex: ReflectiveOperationException) {
            throw RuntimeException(ex)
        }
    }
}