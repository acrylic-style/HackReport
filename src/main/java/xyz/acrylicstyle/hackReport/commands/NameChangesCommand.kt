package xyz.acrylicstyle.hackReport.commands

import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.scheduler.BukkitRunnable
import util.CollectionList
import xyz.acrylicstyle.hackReport.HackReport.Companion.instance
import xyz.acrylicstyle.joinChecker.utils.Utils
import xyz.acrylicstyle.shared.BaseMojangAPI
import xyz.acrylicstyle.shared.NameHistory
import java.util.Calendar
import java.util.function.Consumer

class NameChangesCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        object : BukkitRunnable() {
            override fun run() {
                if (Utils.modCheck(sender)) return
                val uuid = BaseMojangAPI.getUniqueId(args[0])
                if (uuid == null) {
                    sender.sendMessage(ChatColor.RED.toString() + "プレイヤーが見つかりません。")
                    return
                }
                val list = CollectionList<String>()
                BaseMojangAPI.getNameChanges(uuid).reverse().foreach { history: NameHistory, index: Int ->
                    var date = ""
                    if (history.changedToAt != null) {
                        val cal = Calendar.getInstance()
                        cal.timeInMillis = history.changedToAt!!
                        val year = cal[Calendar.YEAR]
                        val month = cal[Calendar.MONTH] + 1
                        val day = cal[Calendar.DAY_OF_MONTH]
                        val hours = cal[Calendar.HOUR_OF_DAY]
                        val minutes = cal[Calendar.MINUTE]
                        val seconds = cal[Calendar.SECOND]
                        date = String.format("%s/%s/%s %s:%s:%s", year, month, day, hours, minutes, seconds)
                    }
                    list.add(ChatColor.YELLOW.toString() + "#" + (index + 1) + ChatColor.GREEN + ": " + ChatColor.GOLD + history.name
                        + "     " + ChatColor.LIGHT_PURPLE + date)
                }
                list.forEach(Consumer { message: String? -> sender.sendMessage(message) })
            }
        }.runTaskLaterAsynchronously(instance, 1)
        return true
    }
}