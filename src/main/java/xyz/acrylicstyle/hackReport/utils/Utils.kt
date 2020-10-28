package xyz.acrylicstyle.hackReport.utils

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import util.CollectionList
import util.ReflectionHelper
import xyz.acrylicstyle.hackReport.HackReport
import xyz.acrylicstyle.hackReport.commands.MuteCommand
import xyz.acrylicstyle.shared.NMSAPI
import xyz.acrylicstyle.shared.OBCAPI
import java.util.Calendar
import java.util.UUID

object Utils {
    @JvmStatic
    val onlinePlayers: CollectionList<Player>
        get() {
            val players = CollectionList<Player>()
            players.addAll(Bukkit.getOnlinePlayers())
            return players
        }

    fun getOnlinePlayers(you: UUID): CollectionList<Player> {
        val players = CollectionList<Player>()
        players.addAll(Bukkit.getOnlinePlayers())
        return players.filter { p: Player -> p.uniqueId != you && !p.isOp }
    }

    private fun getItemStack(material: Material?, displayName: String?, lore: List<String?>?): ItemStack {
        val item = ItemStack(material)
        val meta = item.itemMeta
        if (displayName != null) meta.displayName = displayName
        if (lore != null) meta.lore = lore
        item.itemMeta = meta
        return item
    }

    @JvmStatic
    fun getItemStack(material: Material?, displayName: String?): ItemStack {
        return getItemStack(material, displayName, null)
    }

    @JvmStatic
    val webhook: Webhook?
        get() {
            val url = HackReport.config!!.getString("webhook") ?: return null
            val webhook = Webhook(url)
            webhook.username = "HackReport on " + Bukkit.getVersion()
            return webhook
        }

    fun timestampToDay(timestamp: Long): String {
        if (timestamp <= 0) return "0m"
        val d = timestamp / MuteCommand.DAY
        val h = (timestamp - d * MuteCommand.DAY) / MuteCommand.HOUR
        val m = (timestamp - (d * MuteCommand.DAY + h * MuteCommand.HOUR)) / MuteCommand.MINUTE
        return (if (d < 1) "" else d.toString() + "d") + (if (d < 1 && h < 1) "" else h.toString() + "h") + m + "m"
    }

    fun timestampToDate(timestamp: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        val m = calendar[Calendar.MINUTE].toString()
        val s = calendar[Calendar.SECOND].toString()
        return (calendar[Calendar.YEAR].toString() + "/"
            + (calendar[Calendar.MONTH] + 1) + "/"
            + calendar[Calendar.DAY_OF_MONTH] + " "
            + calendar[Calendar.HOUR_OF_DAY] + ":"
            + (if (m.length == 1) "0$m" else m) + ":"
            + if (s.length == 1) "0$s" else s)
    }

    fun Player.getPing(): String {
        val ep = ReflectionHelper.invokeMethodWithoutException(OBCAPI.getClassWithoutException("entity.CraftPlayer"), player, "getHandle")
        val ping = ReflectionHelper.getFieldWithoutException(NMSAPI.getClassWithoutException("EntityPlayer"), ep, "ping") as Int
        return when {
            ping <= 5 -> "" + ChatColor.LIGHT_PURPLE + ping
            ping <= 50 -> "" + ChatColor.GREEN + ping
            ping <= 150 -> "" + ChatColor.YELLOW + ping
            ping <= 250 -> "" + ChatColor.GOLD + ping
            ping <= 350 -> "" + ChatColor.RED + ping
            else -> "" + ChatColor.DARK_RED + ping
        }
    }
}