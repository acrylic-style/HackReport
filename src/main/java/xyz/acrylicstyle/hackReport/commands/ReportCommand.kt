package xyz.acrylicstyle.hackReport.commands

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import util.ICollectionList
import xyz.acrylicstyle.api.MojangAPI
import xyz.acrylicstyle.hackReport.HackReport
import xyz.acrylicstyle.hackReport.HackReport.Companion.getPlayerInfo
import xyz.acrylicstyle.hackReport.HackReport.Companion.instance
import xyz.acrylicstyle.hackReport.gui.ReportGui
import xyz.acrylicstyle.hackReport.utils.ReportDetails
import xyz.acrylicstyle.hackReport.utils.Utils.onlinePlayers
import xyz.acrylicstyle.hackReport.utils.Utils.webhook
import xyz.acrylicstyle.hackReport.utils.Webhook
import xyz.acrylicstyle.tomeito_api.command.PlayerCommandExecutor
import xyz.acrylicstyle.tomeito_api.sounds.Sound
import java.awt.Color
import java.io.IOException
import java.util.function.Consumer

class ReportCommand : PlayerCommandExecutor() {
    override fun onCommand(player: Player, args: Array<String>) {
        if (!player.hasPermission("hackreport.report")) {
            player.sendMessage("${ChatColor.RED}コマンドを実行する権限がありません。")
            return
        }
        if (args.isEmpty()) {
            player.openInventory(ReportGui().register().prepare(player.uniqueId).inventory)
        } else {
            object : BukkitRunnable() {
                override fun run() {
                    val p = args[0]
                    if (player.name.equals(p, ignoreCase = true)) {
                        player.sendMessage("${ChatColor.RED}自分自身を通報することはできません。")
                        return
                    }
                    val uuid = MojangAPI.getUniqueId(p)
                    if (uuid == null) {
                        player.sendMessage("${ChatColor.RED}プレイヤーが見つかりません。")
                        player.sendMessage("${ChatColor.RED}/report <player> <reason>")
                        return
                    }
                    if (player.uniqueId == uuid) {
                        player.sendMessage("${ChatColor.RED}自分自身を通報することはできません。")
                        return
                    }
                    if (Bukkit.getOfflinePlayer(uuid).isOp) {
                        player.sendMessage("${ChatColor.RED}OPを通報することはできません。")
                        return
                    }
                    object : BukkitRunnable() {
                        override fun run() {
                            val list = ICollectionList.asList(args)
                            list.shift()
                            getPlayerInfo(args[0], uuid).increaseReports()
                            HackReport.REPORTS.add(ReportDetails(args[0], uuid, list))
                            onlinePlayers.filter { obj: Player -> obj.isOp }.forEach(Consumer { p2: Player ->
                                p2.playSound(p2.location, Sound.BLOCK_NOTE_PLING, 100f, 0f)
                                p2.sendMessage("${ChatColor.GREEN}通報: " + ChatColor.RED + args[0] + ChatColor.GREEN + " from " + ChatColor.YELLOW + player.name)
                            })
                            val webhook = webhook ?: return
                            Thread {
                                webhook.addEmbed(
                                    Webhook.EmbedObject()
                                        .apply { title = "通報: `${args[0]}` (from `${player.name}`)" }
                                        .apply { color = Color.RED }
                                        .apply { description = "理由: ${list.join(" ")}" }
                                )
                                try {
                                    webhook.execute()
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }
                            }.start()
                        }
                    }.runTask(instance)
                }
            }.runTaskAsynchronously(instance)
        }
    }
}