package xyz.acrylicstyle.hackReport.commands

import org.bukkit.ChatColor
import org.bukkit.entity.Player
import xyz.acrylicstyle.hackReport.HackReport
import xyz.acrylicstyle.tomeito_api.command.PlayerCommandExecutor

class CommandLogCommand : PlayerCommandExecutor() {
    override fun onCommand(player: Player, args: Array<String>) {
        if (HackReport.commandLog.contains(player.uniqueId)) {
            HackReport.commandLog.remove(player.uniqueId)
            player.sendMessage(ChatColor.YELLOW.toString() + "コマンドログを非表示にしました。")
        } else {
            HackReport.commandLog.add(player.uniqueId)
            player.sendMessage(ChatColor.YELLOW.toString() + "コマンドログを表示するようにしました。")
        }
    }
}