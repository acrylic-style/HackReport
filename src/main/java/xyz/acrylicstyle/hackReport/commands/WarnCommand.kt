package xyz.acrylicstyle.hackReport.commands

import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import util.ICollectionList
import xyz.acrylicstyle.api.MojangAPI
import xyz.acrylicstyle.hackReport.HackReport
import xyz.acrylicstyle.joinChecker.utils.Utils

class WarnCommand : CommandExecutor {
    override fun onCommand(player: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        Thread label@ {
            if (Utils.modCheck(player)) return@label
            if (args.isEmpty()) {
                player.sendMessage(ChatColor.RED.toString() + "/warn <Player> [理由] " + ChatColor.GRAY + "- " + ChatColor.AQUA + "プレイヤーを警告します。")
                return@label
            }
            val uuid = MojangAPI.getUniqueId(args[0])
            if (uuid == null) {
                player.sendMessage(ChatColor.RED.toString() + "プレイヤーが見つかりません。")
                return@label
            }
            if (args.size < 2) {
                HackReport.warnQueue.remove(uuid)
                player.sendMessage(ChatColor.GREEN.toString() + "警告リストから削除しました。")
                return@label
            }
            val list = ICollectionList.asList(args)
            list.shift()
            HackReport.warnQueue.add(uuid, ChatColor.translateAlternateColorCodes('&', list.join(" ")))
            player.sendMessage(ChatColor.GREEN.toString() + "プレイヤーを警告しました: " + list.join(" "))
            player.sendMessage(ChatColor.GRAY.toString() + "プレイヤーがオフラインの場合は次回参加時に警告されます。")
        }.start()
        return true
    }
}