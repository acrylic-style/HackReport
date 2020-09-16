package xyz.acrylicstyle.hackReport.commands

import org.bukkit.ChatColor
import org.bukkit.entity.Player
import xyz.acrylicstyle.hackReport.utils.ConnectionHolder
import xyz.acrylicstyle.tomeito_api.command.PlayerCommandExecutor

class MuteListCommand : PlayerCommandExecutor() {
    override fun onCommand(player: Player, strings: Array<String>) {
        Thread {
            val mutedPpls = ConnectionHolder.muteList.toMap().valuesList().join(ChatColor.YELLOW.toString() + ", " + ChatColor.GREEN)
            player.sendMessage(ChatColor.GREEN.toString() + "ミュートされているプレイヤー:")
            player.sendMessage(ChatColor.GREEN.toString() + mutedPpls)
        }.start()
    }
}