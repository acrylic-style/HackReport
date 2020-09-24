package xyz.acrylicstyle.hackReport.commands

import org.bukkit.ChatColor
import org.bukkit.entity.Player
import xyz.acrylicstyle.hackReport.utils.ConnectionHolder
import xyz.acrylicstyle.joinChecker.utils.Utils
import xyz.acrylicstyle.tomeito_api.command.PlayerCommandExecutor

class MuteListCommand : PlayerCommandExecutor() {
    override fun onCommand(player: Player, strings: Array<String>) {
        Thread t@ {
            if (Utils.modCheck(player)) return@t
            val mutedPpls = ConnectionHolder.muteList.toMap(false).valuesList().join(ChatColor.YELLOW.toString() + ", " + ChatColor.GREEN)
            val mutedPpls2 = ConnectionHolder.muteList.toMap(true).valuesList().join(ChatColor.YELLOW.toString() + ", " + ChatColor.GREEN)
            player.sendMessage(ChatColor.GREEN.toString() + "ミュートされているプレイヤー:")
            player.sendMessage(ChatColor.GREEN.toString() + mutedPpls)
            player.sendMessage(ChatColor.GREEN.toString() + "Tellミュートされているプレイヤー:")
            player.sendMessage(ChatColor.GREEN.toString() + mutedPpls2)
        }.start()
    }
}