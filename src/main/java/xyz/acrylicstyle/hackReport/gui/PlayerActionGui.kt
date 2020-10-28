package xyz.acrylicstyle.hackReport.gui

import net.luckperms.api.node.Node
import org.apache.commons.lang.Validate
import org.bukkit.BanList
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
import org.bukkit.scheduler.BukkitRunnable
import xyz.acrylicstyle.hackReport.HackReport
import xyz.acrylicstyle.hackReport.HackReport.Companion.getPlayerInfo
import xyz.acrylicstyle.hackReport.HackReport.Companion.instance
import xyz.acrylicstyle.hackReport.utils.HackReportPlayer
import xyz.acrylicstyle.hackReport.utils.InventoryUtils
import xyz.acrylicstyle.hackReport.utils.Utils.onlinePlayers
import xyz.acrylicstyle.tomeito_api.sounds.Sound
import xyz.acrylicstyle.tomeito_api.utils.Log
import java.util.UUID
import java.util.function.Consumer
import kotlin.math.roundToInt

class PlayerActionGui : InventoryHolder, Listener {
    @Volatile
    private var uuid: Player? = null

    @Volatile
    private var targetUUID: UUID? = null

    @Volatile
    private var targetName: String? = null
    fun register(): PlayerActionGui {
        Bukkit.getPluginManager().registerEvents(this, instance)
        return this
    }

    fun prepare(reporter: Player, targetName: String, targetUUID: UUID): PlayerActionGui {
        uuid = reporter
        this.targetName = targetName
        this.targetUUID = targetUUID
        return this
    }

    private val items: Inventory
        get() {
            if (uuid == null || targetName == null || targetUUID == null) throw NullPointerException("You must call #prepare first.")
            val inventory = Bukkit.createInventory(this, 27, "")
            val info = ItemStack(Material.PAPER)
            val infoMeta = info.itemMeta
            Validate.notNull(info, "Meta cannot be null")
            val playerInfo = getPlayerInfo(targetName!!, targetUUID!!)
            val player = HackReportPlayer(targetUUID!!)
            infoMeta.displayName = ChatColor.GREEN.toString() + "プレイヤー情報"
            infoMeta.lore = listOf(
                ChatColor.GREEN.toString() + "UUID: " + ChatColor.GOLD + playerInfo.uniqueId.toString(),
                "",
                ChatColor.GOLD.toString() + "通報された回数: " + ChatColor.RED + playerInfo.reports + ChatColor.GOLD + "回",
                "",
                ChatColor.GOLD.toString() + "キル数: " + ChatColor.RED + playerInfo.kills + ChatColor.GOLD + "回",
                ChatColor.GOLD.toString() + "死んだ回数: " + ChatColor.RED + playerInfo.deaths + ChatColor.GOLD + "回",
                "",
                ChatColor.GOLD.toString() + "BANされている: " + ChatColor.RED + if (Bukkit.getOfflinePlayer(playerInfo.uniqueId).isBanned) "はい" else "いいえ",
                ChatColor.GOLD.toString() + "オンライン: " + ChatColor.RED + if (player.isOnline) "はい" else "いいえ",
                ChatColor.GOLD.toString() + "体力: " + ChatColor.RED + (player.health * 100).roundToInt() / 100f,
                ChatColor.GOLD.toString() + "食料レベル: " + ChatColor.RED + player.foodLevel,
                ChatColor.GOLD.toString() + "浮遊可能: " + ChatColor.RED + if (player.allowFlight) "はい" else "いいえ",
                ChatColor.GOLD.toString() + "浮遊している: " + ChatColor.RED + if (player.isFlying) "はい" else "いいえ",
            )
            info.itemMeta = infoMeta
            val ban = ItemStack(Material.WOOL, 1, 5.toShort())
            val banMeta = ban.itemMeta
            Validate.notNull(banMeta, "Meta cannot be null")
            banMeta.displayName = ChatColor.GREEN.toString() + "BANする"
            ban.itemMeta = banMeta
            val kick = ItemStack(Material.WOOL, 1, 5.toShort())
            val kickMeta = kick.itemMeta
            Validate.notNull(kickMeta, "Meta cannot be null")
            kickMeta.displayName = ChatColor.GREEN.toString() + "Kickする"
            kick.itemMeta = kickMeta
            val revoke = ItemStack(Material.WOOL, 1, 5.toShort())
            val revokeMeta = revoke.itemMeta
            Validate.notNull(revokeMeta, "Meta cannot be null")
            revokeMeta.displayName = ChatColor.GREEN.toString() + "通報者の権限を剥奪する"
            revoke.itemMeta = revokeMeta
            val unrevoke = ItemStack(Material.WOOL, 1, 5.toShort())
            val unrevokeMeta = revoke.itemMeta
            Validate.notNull(unrevokeMeta, "Meta cannot be null")
            unrevokeMeta.displayName = ChatColor.GREEN.toString() + "剥奪した通報者の権限を戻す"
            unrevoke.itemMeta = unrevokeMeta
            val red = ItemStack(Material.WOOL, 1, 14.toShort())
            val redMeta = red.itemMeta
            Validate.notNull(redMeta, "Meta cannot be null")
            redMeta.displayName = ChatColor.RED.toString() + "キャンセル"
            red.itemMeta = redMeta
            inventory.setItem(4, info)
            inventory.setItem(11, ban)
            if (HackReport.luckPerms == null) {
                inventory.setItem(13, kick)
            } else {
                inventory.setItem(12, kick)
                inventory.setItem(13, revoke)
                inventory.setItem(14, unrevoke)
            }
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
            Bukkit.getBanList(BanList.Type.NAME).addBan(targetName, null, null, null)
            val player2 = Bukkit.getPlayer(targetUUID)
            player2?.kickPlayer(null)
            onlinePlayers.filter { obj: Player -> obj.isOp }.forEach(Consumer { p: Player -> p.sendMessage(ChatColor.GREEN.toString() + "[HackReport]" + ChatColor.GOLD + player.name + ChatColor.GREEN + "が" + ChatColor.RED + targetName + ChatColor.GREEN + "をBANしました。") })
            Log.info(player.name + " banned " + if (player2 == null) targetUUID else player2.name)
            player.playSound(player.location, Sound.BLOCK_NOTE_PLING, 100f, 2f)
            player.sendMessage(ChatColor.GREEN.toString() + "プレイヤーをBANしました。")
            player.closeInventory()
        }
        if (e.slot == (if (HackReport.luckPerms == null) 13 else 12)) {
            val player2 = Bukkit.getPlayer(targetUUID)
            if (player2 == null) {
                player.sendMessage(ChatColor.RED.toString() + "プレイヤーは現在オンラインではありません。")
                return
            }
            player2.kickPlayer(null)
            Log.info(player.name + " kicked " + player2.name)
            player.playSound(player.location, Sound.BLOCK_NOTE_PLING, 100f, 2f)
            player.sendMessage(ChatColor.GREEN.toString() + "プレイヤーをKickしました。")
            player.closeInventory()
        }
        if (HackReport.luckPerms != null) {
            object : BukkitRunnable() {
                override fun run() {
                    if (e.slot == 13) {
                        val user = HackReport.luckPerms!!.userManager.loadUser(player.uniqueId).join()
                        user.data().add(Node.builder("-hackreport.report").build())
                        HackReport.luckPerms!!.userManager.saveUser(user)
                        player.playSound(player.location, Sound.BLOCK_NOTE_PLING, 100f, 2f)
                        player.sendMessage(ChatColor.RED.toString() + player.name + ChatColor.GREEN + "の通報権限を剥奪しました。")
                        player.closeInventory()
                    } else if (e.slot == 14) {
                        val user = HackReport.luckPerms!!.userManager.loadUser(player.uniqueId).join()
                        user.data().remove(Node.builder("-hackreport.report").build())
                        HackReport.luckPerms!!.userManager.saveUser(user)
                        player.playSound(player.location, Sound.BLOCK_NOTE_PLING, 100f, 2f)
                        player.sendMessage(ChatColor.RED.toString() + player.name + ChatColor.GREEN + "の通報権限の剥奪を取り消しました。")
                        player.closeInventory()
                    }
                }
            }.runTaskAsynchronously(instance)
        }
        if (e.slot == 15) {
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